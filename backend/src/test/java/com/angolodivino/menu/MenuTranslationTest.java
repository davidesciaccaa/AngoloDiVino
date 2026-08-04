package com.angolodivino.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MenuTranslationTest {
    @TempDir Path tempDir;

    @Test
    void createsAndRegeneratesTranslationsInBatchesWithoutChangingStructuralFields() {
        MenuOverridesStore store = MenuServiceTest.store(tempDir);
        RecordingTranslationService translator = new RecordingTranslationService();
        MenuManagementService service = new MenuManagementService(store, translator);

        service.create(command("Nuovo vino", List.of("Servire fresco", "50 cl", "Edizione limitata"), true, null));
        MenuItemResponse created = item(store.readMenu(), "nuovo_vino");

        assertThat(translator.languages).containsExactly("EN", "DE");
        assertThat(created.translations().get("en").notes())
                .containsExactly("EN:Servire fresco", "50 cl", "EN:Edizione limitata");
        assertThat(created.price().options().getFirst().amount()).isEqualByComparingTo("12.5");

        translator.languages.clear();
        service.update(created.id(), command("Vino aggiornato", List.of("Sempre fresco"), true, null));
        MenuItemResponse updated = item(store.readMenu(), created.id());
        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(sectionOf(store.readMenu(), created.id())).isEqualTo("vini");
        assertThat(updated.translations().get("de").name()).isEqualTo("DE:Vino aggiornato");
        assertThat(updated.translations().get("de").notes()).containsExactly("DE:Sempre fresco");
    }

    @Test
    void manualCreateAndUpdateNeverCallTranslatorAndPreserveUnsentLanguages() {
        MenuOverridesStore store = MenuServiceTest.store(tempDir);
        RecordingTranslationService translator = new RecordingTranslationService();
        MenuManagementService service = new MenuManagementService(store, translator);
        Map<String, LocalizedMenuItemText> translations = Map.of(
                "en", text("Wine", "Description", List.of("Fresh")),
                "de", text("Wein", "Beschreibung", List.of("Frisch")));

        service.create(command("Vino", List.of("Fresco"), false, translations));
        MenuItemResponse created = item(store.readMenu(), "vino");
        Map<String, LocalizedMenuItemText> onlyEnglish = Map.of(
                "en", text("Edited wine", "Edited description", List.of("Fresh")));
        service.update(created.id(), command("Vino", List.of("Fresco"), false, onlyEnglish));

        MenuItemResponse updated = item(MenuServiceTest.store(tempDir).readMenu(), created.id());
        assertThat(translator.languages).isEmpty();
        assertThat(updated.translations().get("en").name()).isEqualTo("Edited wine");
        assertThat(updated.translations().get("de").name()).isEqualTo("Wein");
    }

    @Test
    void translationFailureAndMissingKeyLeaveTheFileUntouched() throws Exception {
        MenuOverridesStore store = MenuServiceTest.store(tempDir);
        byte[] before = Files.readAllBytes(store.file());
        MenuManagementService failing = new MenuManagementService(store,
                (texts, language) -> { throw new MenuTranslationException("translation_unavailable", "down"); });

        assertThatThrownBy(() -> failing.create(command("Non salvare", List.of(), true, null)))
                .isInstanceOf(MenuTranslationException.class);
        assertThat(Files.readAllBytes(store.file())).isEqualTo(before);

        TranslationProperties properties = new TranslationProperties();
        properties.setEnabled(true);
        assertThatThrownBy(() -> new DeepLMenuTranslationService(properties).translate(List.of("Ciao"), "EN"))
                .isInstanceOf(MenuTranslationException.class)
                .extracting(error -> ((MenuTranslationException) error).code())
                .isEqualTo("translation_key_missing");
    }

    @Test
    void rejectsUnsupportedTranslationLanguagesBeforeCallingProvider() {
        RecordingTranslationService translator = new RecordingTranslationService();
        MenuManagementService service = new MenuManagementService(MenuServiceTest.store(tempDir), translator);
        assertThatThrownBy(() -> service.create(command("Vino", List.of(), false,
                Map.of("fr", text("Vin", "", List.of())))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("en e de");
        assertThat(translator.languages).isEmpty();
    }

    @Test
    void backfillTranslatesOnlyMissingFieldsPreservesExistingTextAndWritesOnce() {
        CountingWriter writer = new CountingWriter();
        MenuOverridesStore store = MenuServiceTest.store(tempDir, writer);
        MenuManagementService manual = new MenuManagementService(store, new RecordingTranslationService());
        Map<String, LocalizedMenuItemText> partial = new LinkedHashMap<>();
        partial.put("en", new LocalizedMenuItemText("Existing wine", "", null, List.of("Existing note")));
        manual.create(command("Vino", List.of("Nota"), false, partial));
        writer.writes = 0;

        RecordingTranslationService translator = new RecordingTranslationService();
        BackfillTranslationsResponse result = new MenuManagementService(store, translator).backfillMissingTranslations();
        MenuItemResponse updated = item(result.sections(), "vino");

        assertThat(result.updatedItems()).isPositive();
        assertThat(updated.translations().get("en").name()).isEqualTo("Existing wine");
        assertThat(updated.translations().get("en").notes()).containsExactly("Existing note");
        assertThat(updated.translations().get("en").description()).isEqualTo("EN:Descrizione");
        assertThat(writer.writes).isEqualTo(1);
    }

    @Test
    void readsOldJsonAndPersistsTranslationsIntoRuntimeAndBackups() throws Exception {
        Path oldData = tempDir.resolve("old");
        Files.createDirectories(oldData);
        Files.writeString(oldData.resolve("menu.json"), """
                {"updatedAt":"2026-01-01T00:00:00Z","sections":[{"id":"vini","title":"Vini",
                "description":"","items":[{"id":"vino","name":"Vino","subtitle":"","description":"",
                "notes":[],"price":null}]}]}
                """);
        MenuOverridesStore store = MenuServiceTest.store(oldData);
        assertThat(item(store.readMenu(), "vino").translations()).isEmpty();

        new MenuManagementService(store, new RecordingTranslationService()).update("vino",
                command("Vino", List.of(), false, Map.of("en", text("Wine", "", List.of()))));
        store.maintainBackups(java.time.LocalDate.of(2026, 8, 4));

        assertThat(item(MenuServiceTest.store(oldData).readMenu(), "vino").translations().get("en").name())
                .isEqualTo("Wine");
        assertThat(Files.readString(store.dailyBackupDirectory().resolve("menu-2026-08-04.json")))
                .contains("\"translations\"").contains("\"Wine\"");

        new MenuManagementService(store, new RecordingTranslationService()).delete("vino");
        assertThat(store.readMenu().stream().flatMap(section -> section.items().stream())).isEmpty();
    }

    private static MenuItemCommand command(String name, List<String> notes, boolean automatic,
            Map<String, LocalizedMenuItemText> translations) {
        return new MenuItemCommand("vini", name, "Bianchi", "Descrizione", notes,
                MenuPrice.single(new BigDecimal("12.5")), translations, automatic);
    }

    private static LocalizedMenuItemText text(String name, String description, List<String> notes) {
        return new LocalizedMenuItemText(name, "White", description, notes);
    }

    private static MenuItemResponse item(List<MenuSectionResponse> sections, String id) {
        return sections.stream().flatMap(section -> section.items().stream())
                .filter(item -> item.id().equals(id)).findFirst().orElseThrow();
    }

    private static String sectionOf(List<MenuSectionResponse> sections, String id) {
        return sections.stream().filter(section -> section.items().stream().anyMatch(item -> item.id().equals(id)))
                .findFirst().orElseThrow().id();
    }

    private static final class RecordingTranslationService implements MenuTranslationService {
        private final List<String> languages = new ArrayList<>();

        @Override
        public List<String> translate(List<String> texts, String targetLanguage) {
            languages.add(targetLanguage);
            return texts.stream().map(text -> targetLanguage + ":" + text).toList();
        }
    }

    private static final class CountingWriter extends AtomicJsonFileWriter {
        private int writes;

        @Override
        public boolean write(Path target, byte[] content, boolean replaceExisting) throws IOException {
            writes++;
            return super.write(target, content, replaceExisting);
        }
    }
}

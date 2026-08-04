package com.angolodivino.menu;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuManagementService {
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("en", "de");
    private final MenuOverridesStore store;
    private final MenuTranslationService translationService;

    @Autowired
    public MenuManagementService(MenuOverridesStore store, MenuTranslationService translationService) {
        this.store = store;
        this.translationService = translationService;
    }

    MenuManagementService(MenuOverridesStore store) {
        this(store, (texts, language) -> {
            throw new MenuTranslationException("translation_unavailable", "Translation service is not configured");
        });
    }

    public List<MenuSectionResponse> create(MenuItemCommand command) {
        validateTranslations(command.translations(), cleanNotes(command.notes()).size());
        return store.updateMenu(current -> {
            List<MenuSectionResponse> menu = new ArrayList<>(current);
            int sectionIndex = sectionIndex(menu, command.sectionId());
            MenuSectionResponse section = menu.get(sectionIndex);
            List<MenuItemResponse> items = new ArrayList<>(section.items());
            items.add(toItem(uniqueId(menu, command.name()), command, Map.of()));
            menu.set(sectionIndex,
                    new MenuSectionResponse(section.id(), section.title(), section.description(), items));
            return menu;
        });
    }

    public List<MenuSectionResponse> update(String id, MenuItemCommand command) {
        validateTranslations(command.translations(), cleanNotes(command.notes()).size());
        return store.updateMenu(current -> {
            int sourceSectionIndex = -1;
            int sourceItemIndex = -1;
            for (int sectionIndex = 0; sectionIndex < current.size(); sectionIndex++) {
                List<MenuItemResponse> items = current.get(sectionIndex).items();
                for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                    if (items.get(itemIndex).id().equals(id)) {
                        sourceSectionIndex = sectionIndex;
                        sourceItemIndex = itemIndex;
                        break;
                    }
                }
                if (sourceSectionIndex >= 0) {
                    break;
                }
            }
            if (sourceSectionIndex < 0) {
                throw new IllegalArgumentException("Voce di menù sconosciuta: " + id);
            }
            int destinationSectionIndex = sectionIndex(current, command.sectionId());
            List<MenuSectionResponse> menu = new ArrayList<>(current);
            MenuItemResponse existing = current.get(sourceSectionIndex).items().get(sourceItemIndex);
            MenuItemResponse replacement = toItem(id, command, existing.translations());

            if (sourceSectionIndex == destinationSectionIndex) {
                MenuSectionResponse section = current.get(sourceSectionIndex);
                List<MenuItemResponse> items = new ArrayList<>(section.items());
                items.set(sourceItemIndex, replacement);
                menu.set(sourceSectionIndex,
                        new MenuSectionResponse(section.id(), section.title(), section.description(), items));
                return menu;
            }

            MenuSectionResponse source = current.get(sourceSectionIndex);
            List<MenuItemResponse> sourceItems = new ArrayList<>(source.items());
            sourceItems.remove(sourceItemIndex);
            menu.set(sourceSectionIndex,
                    new MenuSectionResponse(source.id(), source.title(), source.description(), sourceItems));

            MenuSectionResponse destination = current.get(destinationSectionIndex);
            List<MenuItemResponse> destinationItems = new ArrayList<>(destination.items());
            destinationItems.add(replacement);
            menu.set(destinationSectionIndex, new MenuSectionResponse(
                    destination.id(), destination.title(), destination.description(), destinationItems));
            return menu;
        });
    }

    public List<MenuSectionResponse> delete(String id) {
        return store.updateMenu(current -> {
            for (int index = 0; index < current.size(); index++) {
                MenuSectionResponse section = current.get(index);
                List<MenuItemResponse> items = new ArrayList<>(section.items());
                if (items.removeIf(item -> item.id().equals(id))) {
                    List<MenuSectionResponse> menu = new ArrayList<>(current);
                    menu.set(index, new MenuSectionResponse(
                            section.id(), section.title(), section.description(), items));
                    return menu;
                }
            }
            throw new IllegalArgumentException("Voce di menù sconosciuta: " + id);
        });
    }

    public BackfillTranslationsResponse backfillMissingTranslations() {
        int[] counts = new int[2];
        List<MenuSectionResponse> sections = store.updateMenu(current -> current.stream()
                .map(section -> new MenuSectionResponse(
                        section.id(), section.title(), section.description(),
                        section.items().stream().map(item -> {
                            LocalizedMenuItemText italian = italian(item.name(), item.subtitle(),
                                    item.description(), item.notes());
                            boolean missing = SUPPORTED_LANGUAGES.stream()
                                    .anyMatch(language -> hasMissing(italian, item.translations().get(language)));
                            if (!missing) {
                                counts[1]++;
                                return item;
                            }
                            Map<String, LocalizedMenuItemText> translated = new LinkedHashMap<>(item.translations());
                            for (String language : List.of("en", "de")) {
                                LocalizedMenuItemText existing = translated.get(language);
                                if (hasMissing(italian, existing)) {
                                    translated.put(language, translateLocalized(italian, existing, language, false));
                                }
                            }
                            counts[0]++;
                            return new MenuItemResponse(item.id(), item.name(), item.subtitle(), item.description(),
                                    item.notes(), item.price(), translated);
                        }).toList()))
                .toList());
        return new BackfillTranslationsResponse(counts[0], counts[1], sections);
    }

    private MenuItemResponse toItem(String id, MenuItemCommand command,
            Map<String, LocalizedMenuItemText> existingTranslations) {
        List<String> notes = cleanNotes(command.notes());
        LocalizedMenuItemText italian = italian(command.name().trim(), text(command.subtitle()),
                text(command.description()), notes);
        Map<String, LocalizedMenuItemText> translations = Boolean.TRUE.equals(command.autoTranslate())
                ? translateAll(italian)
                : mergeManualTranslations(existingTranslations, command.translations(), notes.size());
        return new MenuItemResponse(
                id,
                italian.name(),
                italian.subtitle(),
                italian.description(),
                italian.notes(),
                command.price() == null
                        ? null
                        : command.price().withLabelsForSection(command.sectionId()),
                translations);
    }

    private Map<String, LocalizedMenuItemText> translateAll(LocalizedMenuItemText italian) {
        Map<String, LocalizedMenuItemText> translated = new LinkedHashMap<>();
        translated.put("en", translateLocalized(italian, null, "en", true));
        translated.put("de", translateLocalized(italian, null, "de", true));
        return Map.copyOf(translated);
    }

    private LocalizedMenuItemText translateLocalized(LocalizedMenuItemText italian,
            LocalizedMenuItemText existing, String language, boolean overwrite) {
        String[] fields = {
                initialTranslation(italian.name(), existing == null ? null : existing.name(), overwrite),
                initialTranslation(italian.subtitle(), existing == null ? null : existing.subtitle(), overwrite),
                initialTranslation(italian.description(), existing == null ? null : existing.description(), overwrite)
        };
        List<String> existingNotes = existing == null || existing.notes() == null ? List.of() : existing.notes();
        List<String> notes = new ArrayList<>();
        for (int index = 0; index < italian.notes().size(); index++) {
            notes.add(initialTranslation(italian.notes().get(index),
                    index < existingNotes.size() ? existingNotes.get(index) : null, overwrite));
        }

        List<String> texts = new ArrayList<>();
        List<FieldReference> references = new ArrayList<>();
        String[] italianFields = { italian.name(), italian.subtitle(), italian.description() };
        for (int index = 0; index < italianFields.length; index++) {
            if (shouldTranslate(italianFields[index]) && (overwrite || fields[index].isBlank())) {
                texts.add(italianFields[index]);
                references.add(new FieldReference(index, -1));
            }
        }
        for (int index = 0; index < italian.notes().size(); index++) {
            if (shouldTranslate(italian.notes().get(index)) && (overwrite || notes.get(index).isBlank())) {
                texts.add(italian.notes().get(index));
                references.add(new FieldReference(3, index));
            }
        }

        List<String> results = translationService.translate(texts, language.toUpperCase());
        if (results.size() != texts.size() || results.stream().anyMatch(result -> result == null || result.isBlank())) {
            throw new MenuTranslationException("translation_incomplete",
                    "Il servizio di traduzione ha restituito una risposta incompleta.");
        }
        for (int index = 0; index < results.size(); index++) {
            FieldReference reference = references.get(index);
            String result = results.get(index);
            if (reference.field() < 3) fields[reference.field()] = result;
            else notes.set(reference.noteIndex(), result);
        }
        return new LocalizedMenuItemText(fields[0], fields[1], fields[2], List.copyOf(notes));
    }

    private static boolean hasMissing(LocalizedMenuItemText italian, LocalizedMenuItemText translated) {
        if (translated == null) return true;
        if (shouldTranslate(italian.name()) && value(translated.name()).isBlank()) return true;
        if (shouldTranslate(italian.subtitle()) && value(translated.subtitle()).isBlank()) return true;
        if (shouldTranslate(italian.description()) && value(translated.description()).isBlank()) return true;
        List<String> notes = translated.notes() == null ? List.of() : translated.notes();
        if (notes.size() != italian.notes().size()) return true;
        for (int index = 0; index < italian.notes().size(); index++) {
            if (shouldTranslate(italian.notes().get(index)) && value(notes.get(index)).isBlank()) return true;
        }
        return false;
    }

    private static Map<String, LocalizedMenuItemText> mergeManualTranslations(
            Map<String, LocalizedMenuItemText> existing,
            Map<String, LocalizedMenuItemText> requested,
            int noteCount) {
        Map<String, LocalizedMenuItemText> merged = new LinkedHashMap<>(existing == null ? Map.of() : existing);
        if (requested == null) return Map.copyOf(merged);
        requested.forEach((language, incoming) -> {
            LocalizedMenuItemText previous = merged.get(language);
            merged.put(language, new LocalizedMenuItemText(
                    incoming.name() == null ? value(previous == null ? null : previous.name()) : text(incoming.name()),
                    incoming.subtitle() == null ? value(previous == null ? null : previous.subtitle()) : text(incoming.subtitle()),
                    incoming.description() == null ? value(previous == null ? null : previous.description()) : text(incoming.description()),
                    incoming.notes() == null
                            ? normalizedNotes(previous == null ? null : previous.notes(), noteCount)
                            : incoming.notes().stream().map(MenuManagementService::text).toList()));
        });
        return Map.copyOf(merged);
    }

    private static void validateTranslations(Map<String, LocalizedMenuItemText> translations, int noteCount) {
        if (translations == null) return;
        for (Map.Entry<String, LocalizedMenuItemText> entry : translations.entrySet()) {
            if (!SUPPORTED_LANGUAGES.contains(entry.getKey())) {
                throw new IllegalArgumentException("Sono ammesse soltanto le traduzioni en e de");
            }
            if (entry.getValue() == null) {
                throw new IllegalArgumentException("Traduzione non valida per " + entry.getKey());
            }
            if (entry.getValue().notes() != null && entry.getValue().notes().size() != noteCount) {
                throw new IllegalArgumentException("Le note tradotte devono corrispondere alle note italiane");
            }
        }
    }

    private static LocalizedMenuItemText italian(String name, String subtitle, String description, List<String> notes) {
        return new LocalizedMenuItemText(name, subtitle, description, notes);
    }

    private static List<String> cleanNotes(List<String> notes) {
        return notes == null ? List.of() : notes.stream().filter(note -> note != null)
                .map(String::trim).filter(note -> !note.isEmpty()).toList();
    }

    private static List<String> normalizedNotes(List<String> notes, int noteCount) {
        if (notes == null) return List.of();
        return notes.stream().limit(noteCount).map(MenuManagementService::text).toList();
    }

    private static String value(String value) {
        return value == null ? "" : value;
    }

    private static String initialTranslation(String italian, String existing, boolean overwrite) {
        if (!shouldTranslate(italian)) return value(italian);
        return overwrite ? "" : value(existing);
    }

    private static boolean shouldTranslate(String text) {
        return text != null && !text.isBlank()
                && !text.trim().matches("(?i)^\\d+(?:[.,]\\d+)?\\s*(?:cl|ml|l|€)?$");
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }

    private record FieldReference(int field, int noteIndex) { }

    private static int sectionIndex(List<MenuSectionResponse> menu, String id) {
        for (int index = 0; index < menu.size(); index++) {
            if (menu.get(index).id().equals(id)) {
                return index;
            }
        }
        throw new IllegalArgumentException("Categoria sconosciuta: " + id);
    }

    private static MenuItemResponse find(List<MenuSectionResponse> menu, String id) {
        return menu.stream()
                .flatMap(section -> section.items().stream())
                .filter(item -> item.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    private static String uniqueId(List<MenuSectionResponse> menu, String name) {
        String base = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("(^_|_$)", "");
        if (base.isBlank()) {
            base = "piatto";
        }
        String id = base;
        for (int suffix = 2; find(menu, id) != null; suffix++) {
            id = base + "_" + suffix;
        }
        return id;
    }
}

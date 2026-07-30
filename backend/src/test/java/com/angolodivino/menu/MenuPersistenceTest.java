package com.angolodivino.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MenuPersistenceTest {
    @TempDir Path tempDir;

    @Test
    void createsDirectoriesAndInitializesRuntimeMenuFromDefault() {
        Path data = tempDir.resolve("nested").resolve("data");
        MenuOverridesStore store = MenuServiceTest.store(data);

        assertThat(store.dataDirectory()).isEqualTo(data.toAbsolutePath().normalize());
        assertThat(store.file()).isRegularFile();
        assertThat(read(store.file())).isNotEmpty();
        assertThat(store.dailyBackupDirectory()).isDirectory();
        assertThat(store.monthlyBackupDirectory()).isDirectory();
        assertThat(store.readMenu()).isEqualTo(store.readDefaultMenu());
    }

    @Test
    void neverOverwritesAnExistingRuntimeMenuDuringRestart() {
        MenuOverridesStore first = MenuServiceTest.store(tempDir);
        new MenuService(first).updatePrices(java.util.Map.of("negroni", "99 €"));
        byte[] persisted = read(first.file());

        MenuOverridesStore restarted = MenuServiceTest.store(tempDir);

        assertThat(read(restarted.file())).isEqualTo(persisted);
        assertThat(MenuServiceTest.priceOf(restarted.readMenu(), "negroni")).isEqualTo("99 €");
    }

    @Test
    void migratesThePreviousPriceOverrideFileAutomatically() throws Exception {
        Files.writeString(
                tempDir.resolve("menu-overrides.json"),
                "{\"prices\":{\"negroni\":\"42 €\"}}");

        MenuOverridesStore store = MenuServiceTest.store(tempDir);

        assertThat(MenuServiceTest.priceOf(store.readMenu(), "negroni")).isEqualTo("42 €");
        assertThat(store.file()).isRegularFile();
        assertThat(tempDir.resolve("menu-overrides.json")).isRegularFile();
    }

    @Test
    void startupRecoveryCreatesMissingDailyAndMonthlyBackups() {
        MenuOverridesStore store = MenuServiceTest.store(tempDir);

        new MenuBackupService(store).recoverMissingBackupsAtStartup();

        LocalDate todayInRome = LocalDate.now(MenuBackupService.ROME);
        assertThat(store.dailyBackupDirectory().resolve("menu-" + todayInRome + ".json")).isRegularFile();
        assertThat(store.monthlyBackupDirectory()
                .resolve("menu-" + java.time.YearMonth.from(todayInRome) + ".json")).isRegularFile();
    }

    @Test
    void persistsCreateUpdateDeleteAndSurvivesRestart() {
        MenuOverridesStore store = MenuServiceTest.store(tempDir);
        MenuManagementService management = new MenuManagementService(store);
        management.create(command("Nuovo piatto", "12.50"));
        assertThat(item(store.readMenu(), "nuovo_piatto").name()).isEqualTo("Nuovo piatto");

        management.update("nuovo_piatto", command("Piatto modificato", "14"));
        MenuOverridesStore restarted = MenuServiceTest.store(tempDir);
        assertThat(item(restarted.readMenu(), "nuovo_piatto").name()).isEqualTo("Piatto modificato");
        assertThat(item(restarted.readMenu(), "nuovo_piatto").price()).isEqualTo("14");

        new MenuManagementService(restarted).delete("nuovo_piatto");
        assertThat(find(MenuServiceTest.store(tempDir).readMenu(), "nuovo_piatto")).isNull();
    }

    @Test
    void publishesThroughATemporaryFileInTheSameDirectory() {
        RecordingWriter writer = new RecordingWriter();
        MenuOverridesStore store = MenuServiceTest.store(tempDir, writer);

        new MenuService(store).updatePrices(java.util.Map.of("negroni", "10 €"));

        assertThat(writer.observedTemporary).isNotNull();
        assertThat(writer.observedTemporary.getParent()).isEqualTo(store.file().getParent());
        assertThat(writer.temporaryExistedBeforeMove).isTrue();
        assertThat(Files.exists(writer.observedTemporary)).isFalse();
    }

    @Test
    void aWriteFailureLeavesThePreviousMenuIntactAndCleansTemporaryFiles() {
        SwitchableFailingWriter writer = new SwitchableFailingWriter();
        MenuOverridesStore store = MenuServiceTest.store(tempDir, writer);
        byte[] original = read(store.file());
        writer.fail = true;

        assertThatThrownBy(() -> new MenuService(store).updatePrices(java.util.Map.of("negroni", "10 €")))
                .isInstanceOf(UncheckedIOException.class);
        assertThat(read(store.file())).isEqualTo(original);
        assertThat(tempFiles(tempDir)).isEmpty();
    }

    @Test
    void createsOnlyOneDailyBackupAndDoesNotOverwriteIt() throws Exception {
        MenuOverridesStore store = MenuServiceTest.store(tempDir);
        LocalDate date = LocalDate.of(2026, 7, 30);
        store.maintainBackups(date);
        Path backup = store.dailyBackupDirectory().resolve("menu-2026-07-30.json");
        byte[] firstState = read(backup);

        new MenuService(store).updatePrices(java.util.Map.of("negroni", "12 €"));
        store.maintainBackups(date);

        assertThat(Files.list(store.dailyBackupDirectory()).toList()).hasSize(1);
        assertThat(read(backup)).isEqualTo(firstState);
    }

    @Test
    void createsOnlyTheFirstMonthlyStateAndDoesNotOverwriteIt() throws Exception {
        MenuOverridesStore store = MenuServiceTest.store(tempDir);
        store.maintainBackups(LocalDate.of(2026, 7, 1));
        Path backup = store.monthlyBackupDirectory().resolve("menu-2026-07.json");
        byte[] firstState = read(backup);

        new MenuService(store).updatePrices(java.util.Map.of("negroni", "12 €"));
        store.maintainBackups(LocalDate.of(2026, 7, 31));

        assertThat(Files.list(store.monthlyBackupDirectory()).toList()).hasSize(1);
        assertThat(read(backup)).isEqualTo(firstState);
    }

    @Test
    void removesDailyBackupsOutsideTheThirtyDayWindow() {
        MenuOverridesStore store = MenuServiceTest.store(tempDir);
        LocalDate today = LocalDate.of(2026, 7, 30);
        store.maintainBackups(today.minusDays(30));
        store.maintainBackups(today.minusDays(29));
        store.maintainBackups(today);

        assertThat(store.dailyBackupDirectory().resolve("menu-2026-06-30.json")).doesNotExist();
        assertThat(store.dailyBackupDirectory().resolve("menu-2026-07-01.json")).exists();
        assertThat(store.dailyBackupDirectory().resolve("menu-2026-07-30.json")).exists();
    }

    @Test
    void keepsTheLatestTwelveMonthlyBackups() throws Exception {
        MenuOverridesStore store = MenuServiceTest.store(tempDir);
        LocalDate current = LocalDate.of(2026, 12, 15);
        for (int monthsAgo = 13; monthsAgo >= 0; monthsAgo--) {
            store.maintainBackups(current.minusMonths(monthsAgo));
        }

        assertThat(store.monthlyBackupDirectory().resolve("menu-2025-11.json")).doesNotExist();
        assertThat(store.monthlyBackupDirectory().resolve("menu-2026-01.json")).exists();
        assertThat(Files.list(store.monthlyBackupDirectory()).toList()).hasSize(12);
    }

    @Test
    void doesNotCreateBackupsFromACorruptRuntimeMenu() throws Exception {
        MenuOverridesStore store = MenuServiceTest.store(tempDir);
        Files.writeString(store.file(), "{broken");

        assertThatThrownBy(() -> store.maintainBackups(LocalDate.of(2026, 7, 30)))
                .isInstanceOf(UncheckedIOException.class);
        assertThat(Files.list(store.dailyBackupDirectory()).toList()).isEmpty();
        assertThat(Files.list(store.monthlyBackupDirectory()).toList()).isEmpty();
    }

    @Test
    void concurrentCrudAndBackupsAlwaysLeaveACompleteValidMenu() throws Exception {
        MenuOverridesStore store = MenuServiceTest.store(tempDir);
        MenuManagementService management = new MenuManagementService(store);
        var executor = Executors.newFixedThreadPool(8);
        List<Callable<Void>> jobs = IntStream.range(0, 20)
                .mapToObj(index -> (Callable<Void>) () -> {
                    management.create(command("Concorrente " + index, String.valueOf(index + 1)));
                    store.maintainBackups(LocalDate.of(2026, 7, 30));
                    return null;
                })
                .toList();
        try {
            for (var future : executor.invokeAll(jobs)) {
                future.get();
            }
        } finally {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }

        assertThat(store.readMenu().stream().flatMap(section -> section.items().stream())
                .filter(menuItem -> menuItem.id().startsWith("concorrente_"))).hasSize(20);
        MenuOverridesStore.validateDocument(new com.fasterxml.jackson.databind.ObjectMapper()
                .findAndRegisterModules()
                .readValue(store.file().toFile(), MenuOverridesDocument.class));
        assertThat(store.dailyBackupDirectory().resolve("menu-2026-07-30.json")).isRegularFile();
    }

    private static MenuItemCommand command(String name, String price) {
        return new MenuItemCommand(
                "aperitivo", name, "Test", "Descrizione", List.of("Nota"), new BigDecimal(price));
    }

    private static MenuItemResponse item(List<MenuSectionResponse> menu, String id) {
        MenuItemResponse found = find(menu, id);
        if (found == null) throw new AssertionError("Missing item " + id);
        return found;
    }

    private static MenuItemResponse find(List<MenuSectionResponse> menu, String id) {
        return menu.stream().flatMap(section -> section.items().stream())
                .filter(menuItem -> menuItem.id().equals(id)).findFirst().orElse(null);
    }

    private static byte[] read(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<Path> tempFiles(Path directory) {
        try (var files = Files.walk(directory)) {
            return files.filter(path -> path.getFileName().toString().endsWith(".tmp")).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class RecordingWriter extends AtomicJsonFileWriter {
        Path observedTemporary;
        boolean temporaryExistedBeforeMove;

        @Override
        protected boolean moveIntoPlace(Path temporary, Path target, boolean replaceExisting) throws IOException {
            observedTemporary = temporary;
            temporaryExistedBeforeMove = Files.isRegularFile(temporary);
            return super.moveIntoPlace(temporary, target, replaceExisting);
        }
    }

    private static final class SwitchableFailingWriter extends AtomicJsonFileWriter {
        boolean fail;

        @Override
        protected boolean moveIntoPlace(Path temporary, Path target, boolean replaceExisting) throws IOException {
            if (fail) {
                throw new IOException("simulated move failure");
            }
            return super.moveIntoPlace(temporary, target, replaceExisting);
        }
    }
}

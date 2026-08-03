package com.angolodivino.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Owns the runtime JSON and the single lock coordinating reads, CRUD writes and backups.
 */
@Component
public class MenuOverridesStore {
    private static final Logger log = LoggerFactory.getLogger(MenuOverridesStore.class);
    private static final String MENU_FILE_NAME = "menu.json";

    private final MenuProperties properties;
    private final Resource defaultMenuResource;
    private final ObjectMapper objectMapper;
    private final AtomicJsonFileWriter fileWriter;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final Path dataDirectory;
    private final Path menuFile;
    private final Path dailyBackupDirectory;
    private final Path monthlyBackupDirectory;
    private final Path legacyOverridesFile;

    @Autowired
    public MenuOverridesStore(
            MenuProperties properties,
            ResourceLoader resourceLoader,
            ObjectMapper objectMapper,
            AtomicJsonFileWriter fileWriter) {
        this(properties, resourceLoader.getResource(properties.getDefaultResource()), objectMapper, fileWriter);
    }

    MenuOverridesStore(
            MenuProperties properties,
            Resource defaultMenuResource,
            ObjectMapper objectMapper,
            AtomicJsonFileWriter fileWriter) {
        this.properties = properties;
        this.defaultMenuResource = defaultMenuResource;
        this.objectMapper = objectMapper.copy();
        this.fileWriter = fileWriter;
        this.dataDirectory = Path.of(properties.getDataDirectory()).toAbsolutePath().normalize();
        this.menuFile = dataDirectory.resolve(MENU_FILE_NAME);
        this.dailyBackupDirectory = dataDirectory.resolve("backups").resolve("daily");
        this.monthlyBackupDirectory = dataDirectory.resolve("backups").resolve("monthly");
        this.legacyOverridesFile = resolveLegacyFile(properties.getLegacyOverridesFile());
    }

    @PostConstruct
    public void initialize() {
        lock.writeLock().lock();
        try {
            Files.createDirectories(dataDirectory);
            Files.createDirectories(dailyBackupDirectory);
            Files.createDirectories(monthlyBackupDirectory);

            if (Files.notExists(menuFile)) {
                MenuOverridesDocument initial = loadInitialDocument();
                writeDocumentUnlocked(initial.sections());
                log.info("Runtime menu initialized at {}", menuFile);
            } else {
                readDocument(menuFile);
                log.info("Existing runtime menu loaded from {}; it was not overwritten", menuFile);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not initialize menu persistence at " + dataDirectory, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Path dataDirectory() {
        return dataDirectory;
    }

    public Path file() {
        return menuFile;
    }

    public Path dailyBackupDirectory() {
        return dailyBackupDirectory;
    }

    public Path monthlyBackupDirectory() {
        return monthlyBackupDirectory;
    }

    public List<MenuSectionResponse> readMenu() {
        lock.readLock().lock();
        try {
            return readDocument(menuFile).sections();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read runtime menu " + menuFile, e);
        } finally {
            lock.readLock().unlock();
        }
    }

    List<MenuSectionResponse> readDefaultMenu() {
        lock.readLock().lock();
        try {
            return readDefaultDocument().sections();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read default menu " + defaultMenuResource, e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Executes a complete read-modify-validate-write cycle while holding the same lock used by backups.
     */
    public List<MenuSectionResponse> updateMenu(UnaryOperator<List<MenuSectionResponse>> operation) {
        lock.writeLock().lock();
        try {
            List<MenuSectionResponse> current = readDocument(menuFile).sections();
            List<MenuSectionResponse> updated = operation.apply(current);
            validateSections(updated);
            writeDocumentUnlocked(updated);
            return updated;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not update runtime menu " + menuFile, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Creates missing backups and applies retention while exclusively holding the menu lock.
     */
    public void maintainBackups(LocalDate date) {
        lock.writeLock().lock();
        try {
            byte[] validMenu = readValidMenuBytes();
            createBackupIfNeeded(
                    dailyBackupDirectory.resolve("menu-" + date + ".json"),
                    validMenu,
                    "daily");
            createBackupIfNeeded(
                    monthlyBackupDirectory.resolve("menu-" + YearMonth.from(date) + ".json"),
                    validMenu,
                    "monthly");
            cleanDailyBackups(date);
            cleanMonthlyBackups(YearMonth.from(date));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not maintain menu backups", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private MenuOverridesDocument loadInitialDocument() throws IOException {
        MenuOverridesDocument defaults = readDefaultDocument();
        if (legacyOverridesFile == null || Files.notExists(legacyOverridesFile)) {
            return defaults;
        }

        MenuOverridesDocument legacy = readDocumentAllowingLegacy(legacyOverridesFile);
        if (legacy.sections() != null) {
            log.info("Migrating complete legacy menu from {}", legacyOverridesFile);
            return new MenuOverridesDocument(Instant.now(), legacy.sections(), null);
        }
        if (legacy.prices() != null && !legacy.prices().isEmpty()) {
            log.info("Migrating {} legacy price override(s) from {}", legacy.prices().size(), legacyOverridesFile);
            return new MenuOverridesDocument(
                    Instant.now(),
                    applyLegacyPrices(defaults.sections(), legacy.prices()),
                    null);
        }
        return defaults;
    }

    private MenuOverridesDocument readDefaultDocument() throws IOException {
        if (!defaultMenuResource.exists()) {
            throw new IOException("Default menu resource does not exist: " + defaultMenuResource);
        }
        try (InputStream input = defaultMenuResource.getInputStream()) {
            MenuOverridesDocument document = objectMapper.readValue(input, MenuOverridesDocument.class);
            validateDocument(document);
            return document;
        }
    }

    private MenuOverridesDocument readDocument(Path path) throws IOException {
        if (!Files.isRegularFile(path) || Files.size(path) == 0) {
            throw new IOException("Menu JSON is missing or empty: " + path);
        }
        MenuOverridesDocument document = objectMapper.readValue(path.toFile(), MenuOverridesDocument.class);
        validateDocument(document);
        return document;
    }

    private MenuOverridesDocument readDocumentAllowingLegacy(Path path) throws IOException {
        if (!Files.isRegularFile(path) || Files.size(path) == 0) {
            throw new IOException("Legacy menu JSON is missing or empty: " + path);
        }
        MenuOverridesDocument document = objectMapper.readValue(path.toFile(), MenuOverridesDocument.class);
        if (document == null || (document.sections() == null && document.prices() == null)) {
            throw new IOException("Legacy menu JSON has no supported content: " + path);
        }
        if (document.sections() != null) {
            validateSections(document.sections());
        }
        return document;
    }

    private byte[] readValidMenuBytes() throws IOException {
        MenuOverridesDocument document = readDocument(menuFile);
        validateDocument(document);
        return Files.readAllBytes(menuFile);
    }

    private void writeDocumentUnlocked(List<MenuSectionResponse> sections) throws IOException {
        validateSections(sections);
        MenuOverridesDocument document = new MenuOverridesDocument(Instant.now(), sections, null);
        byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(document);
        // Deserialize the exact bytes that will be published, not only the in-memory object.
        MenuOverridesDocument serialized = objectMapper.readValue(json, MenuOverridesDocument.class);
        validateDocument(serialized);
        fileWriter.write(menuFile, json, true);
    }

    private void createBackupIfNeeded(Path target, byte[] validMenu, String kind) throws IOException {
        if (Files.exists(target)) {
            try {
                readDocument(target);
                return;
            } catch (IOException e) {
                log.error("Existing {} backup {} is invalid and will be replaced", kind, target, e);
            }
        }
        fileWriter.write(target, validMenu, true);
        readDocument(target);
        log.info("Created {} menu backup {}", kind, target);
    }

    private void cleanDailyBackups(LocalDate today) throws IOException {
        LocalDate oldestKept = today.minusDays(properties.getDailyBackupRetention() - 1L);
        try (DirectoryStream<Path> files = Files.newDirectoryStream(dailyBackupDirectory, "menu-*.json")) {
            for (Path file : files) {
                LocalDate backupDate = parseDailyDate(file);
                if (backupDate != null && backupDate.isBefore(oldestKept)) {
                    Files.deleteIfExists(file);
                    log.info("Deleted expired daily menu backup {}", file);
                }
            }
        }
    }

    private void cleanMonthlyBackups(YearMonth currentMonth) throws IOException {
        YearMonth oldestKept = currentMonth.minusMonths(properties.getMonthlyBackupRetention() - 1L);
        try (DirectoryStream<Path> files = Files.newDirectoryStream(monthlyBackupDirectory, "menu-*.json")) {
            for (Path file : files) {
                YearMonth backupMonth = parseMonthlyDate(file);
                if (backupMonth != null && backupMonth.isBefore(oldestKept)) {
                    Files.deleteIfExists(file);
                    log.info("Deleted expired monthly menu backup {}", file);
                }
            }
        }
    }

    private static LocalDate parseDailyDate(Path file) {
        String name = file.getFileName().toString();
        try {
            return LocalDate.parse(name.substring(5, name.length() - 5));
        } catch (DateTimeParseException | IndexOutOfBoundsException e) {
            return null;
        }
    }

    private static YearMonth parseMonthlyDate(Path file) {
        String name = file.getFileName().toString();
        try {
            return YearMonth.parse(name.substring(5, name.length() - 5));
        } catch (DateTimeParseException | IndexOutOfBoundsException e) {
            return null;
        }
    }

    private Path resolveLegacyFile(String configured) {
        if (configured == null || configured.isBlank()) {
            return dataDirectory.resolve("menu-overrides.json");
        }
        return Path.of(configured).toAbsolutePath().normalize();
    }

    private static List<MenuSectionResponse> applyLegacyPrices(
            List<MenuSectionResponse> sections,
            Map<String, String> prices) {
        return sections.stream()
                .map(section -> new MenuSectionResponse(
                        section.id(),
                        section.title(),
                        section.description(),
                        section.items().stream()
                                .map(item -> prices.containsKey(item.id())
                                        ? new MenuItemResponse(
                                                item.id(),
                                                item.name(),
                                                item.subtitle(),
                                                item.description(),
                                                item.notes(),
                                                legacyPrice(prices.get(item.id()), section.id()))
                                        : item)
                                .toList()))
                .toList();
    }

    private static MenuPrice legacyPrice(String price, String sectionId) {
        MenuPrice parsed = MenuPriceDeserializer.parseLegacy(price);
        return parsed == null ? null : parsed.withLabelsForSection(sectionId);
    }

    static void validateDocument(MenuOverridesDocument document) throws IOException {
        if (document == null || document.sections() == null) {
            throw new IOException("Menu JSON does not contain sections");
        }
        validateSections(document.sections());
    }

    static void validateSections(List<MenuSectionResponse> sections) throws IOException {
        if (sections == null || sections.isEmpty()) {
            throw new IOException("The menu must contain at least one section");
        }
        Set<String> sectionIds = new HashSet<>();
        Set<String> itemIds = new HashSet<>();
        for (MenuSectionResponse section : sections) {
            if (section == null
                    || isBlank(section.id())
                    || isBlank(section.title())
                    || section.description() == null
                    || section.items() == null) {
                throw new IOException("Invalid menu section");
            }
            if (!sectionIds.add(section.id())) {
                throw new IOException("Duplicate menu section id: " + section.id());
            }
            for (MenuItemResponse item : section.items()) {
                if (item == null
                        || isBlank(item.id())
                        || isBlank(item.name())
                        || item.subtitle() == null
                        || item.description() == null
                        || item.notes() == null) {
                    throw new IOException("Invalid menu item in section " + section.id());
                }
                if (!itemIds.add(item.id())) {
                    throw new IOException("Duplicate menu item id: " + item.id());
                }
                if (item.notes().stream().anyMatch(note -> note == null || note.isBlank())) {
                    throw new IOException("Invalid note in menu item " + item.id());
                }
            }
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

package com.angolodivino.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** JSON persistence for the editable menu, written atomically. */
@Component
public class MenuOverridesStore {
    private static final Logger log = LoggerFactory.getLogger(MenuOverridesStore.class);
    private final ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).build();
    private final Path file;

    public MenuOverridesStore(MenuProperties properties) {
        file = Path.of(properties.getOverridesFile()).toAbsolutePath().normalize();
    }
    @PostConstruct void logStartupState() { log.info("Menu storage: {}", file); }
    public Path file() { return file; }

    public synchronized List<MenuSectionResponse> readMenu(List<MenuSectionResponse> defaults) {
        if (!Files.isRegularFile(file)) return defaults;
        try {
            MenuOverridesDocument document = objectMapper.readValue(file.toFile(), MenuOverridesDocument.class);
            if (document != null && document.sections() != null) return document.sections();
            if (document != null && document.prices() != null) return migrateLegacyPrices(defaults, document.prices());
        } catch (IOException e) { log.warn("Could not read menu storage {}, using defaults", file, e); }
        return defaults;
    }

    /** Compatibility accessors used by the original price-only service. */
    public Map<String, String> readPrices() {
        if (!Files.isRegularFile(file)) return Map.of();
        try {
            MenuOverridesDocument document = objectMapper.readValue(file.toFile(), MenuOverridesDocument.class);
            return document == null || document.prices() == null ? Map.of() : new LinkedHashMap<>(document.prices());
        } catch (IOException e) { return Map.of(); }
    }

    public synchronized void writePrices(Map<String, String> prices) {
        writeDocument(new MenuOverridesDocument(Instant.now(), null, prices));
    }

    /** Reads old {prices:{id:"9 €"}} files and returns numeric values without rewriting until an edit. */
    private static List<MenuSectionResponse> migrateLegacyPrices(List<MenuSectionResponse> defaults, Map<String, String> prices) {
        return defaults.stream().map(section -> new MenuSectionResponse(section.id(), section.title(), section.description(),
                section.items().stream().map(item -> {
                    String value = prices.get(item.id());
                    if (value == null) return item;
                    return new MenuItemResponse(item.id(), item.name(), item.subtitle(), item.description(), item.notes(), value);
                }).toList())).toList();
    }
    public synchronized void writeMenu(List<MenuSectionResponse> sections) {
        writeDocument(new MenuOverridesDocument(Instant.now(), sections, null));
    }
    private void writeDocument(MenuOverridesDocument document) {
        try {
            Path parent = file.getParent(); if (parent != null) Files.createDirectories(parent);
            Path temp = Files.createTempFile(parent, "menu-overrides", ".json.tmp");
            try { objectMapper.writerWithDefaultPrettyPrinter().writeValue(temp.toFile(), document); move(temp, file); }
            finally { Files.deleteIfExists(temp); }
        } catch (IOException e) { throw new UncheckedIOException("Could not write menu to " + file, e); }
    }
    private static void move(Path from, Path to) throws IOException {
        try { Files.move(from, to, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
        catch (AtomicMoveNotSupportedException e) { Files.move(from, to, StandardCopyOption.REPLACE_EXISTING); }
    }
}

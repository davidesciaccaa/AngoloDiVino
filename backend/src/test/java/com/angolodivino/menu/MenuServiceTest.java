package com.angolodivino.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;

class MenuServiceTest {
    @TempDir Path tempDir;
    private MenuOverridesStore store;
    private MenuService service;

    @BeforeEach
    void setUp() {
        store = store(tempDir);
        service = new MenuService(store);
    }

    @Test
    void readsTheVersionedDefaultMenuInOrder() {
        assertThat(service.findMenuSections())
                .extracting(MenuSectionResponse::id)
                .containsExactly("aperitivo", "drink", "vini", "amari", "superalcolici", "bevande");
    }

    @Test
    void itemIdsAreUniqueAcrossSections() {
        List<String> ids = store.readDefaultMenu().stream()
                .flatMap(section -> section.items().stream())
                .map(MenuItemResponse::id)
                .toList();
        assertThat(ids).doesNotHaveDuplicates();
    }

    @Test
    void priceOnlyUpdatesArePersistedInTheCompleteMenu() {
        service.updatePrices(Map.of("negroni", "9 €"));
        assertThat(priceOf(service.findMenuSections(), "negroni")).isEqualTo("9 €");

        MenuService restarted = new MenuService(store(tempDir));
        assertThat(priceOf(restarted.findMenuSections(), "negroni")).isEqualTo("9 €");
    }

    @Test
    void rejectsUnknownItemsAndMalformedPrices() {
        assertThatThrownBy(() -> service.updatePrices(Map.of("missing", "9 €")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing");
        assertThatThrownBy(() -> service.updatePrices(Map.of("negroni", "<script>")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negroni");
    }

    static MenuOverridesStore store(Path dataDirectory) {
        return store(dataDirectory, new AtomicJsonFileWriter());
    }

    static MenuOverridesStore store(Path dataDirectory, AtomicJsonFileWriter writer) {
        MenuProperties properties = new MenuProperties();
        properties.setDataDirectory(dataDirectory.toString());
        ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        MenuOverridesStore store = new MenuOverridesStore(
                properties,
                new ClassPathResource("menu.default.json"),
                mapper,
                writer);
        store.initialize();
        return store;
    }

    static String priceOf(List<MenuSectionResponse> sections, String itemId) {
        return sections.stream()
                .flatMap(section -> section.items().stream())
                .filter(item -> item.id().equals(itemId))
                .findFirst()
                .map(MenuItemResponse::price)
                .orElseThrow();
    }
}

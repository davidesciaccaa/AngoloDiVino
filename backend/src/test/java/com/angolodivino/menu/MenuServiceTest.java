package com.angolodivino.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
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
        MenuPrice originalWinePrice = priceOf(service.findMenuSections(), "tacco_barocco_bianco");
        service.updatePrices(Map.of("negroni", MenuPrice.single(new BigDecimal("9"))));
        assertThat(amountsOf(service.findMenuSections(), "negroni")).containsExactly(new BigDecimal("9"));
        assertThat(priceOf(service.findMenuSections(), "tacco_barocco_bianco"))
                .isEqualTo(originalWinePrice);

        MenuService restarted = new MenuService(store(tempDir));
        assertThat(amountsOf(restarted.findMenuSections(), "negroni")).containsExactly(new BigDecimal("9"));
        assertThat(priceOf(restarted.findMenuSections(), "tacco_barocco_bianco"))
                .isEqualTo(originalWinePrice);
    }

    @Test
    void rejectsUnknownItemsAndEmptyUpdates() {
        assertThatThrownBy(() -> service.updatePrices(Map.of("missing", MenuPrice.single(BigDecimal.ONE))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing");
        assertThatThrownBy(() -> service.updatePrices(Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("almeno un prezzo");
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

    static MenuPrice priceOf(List<MenuSectionResponse> sections, String itemId) {
        return sections.stream()
                .flatMap(section -> section.items().stream())
                .filter(item -> item.id().equals(itemId))
                .findFirst()
                .orElseThrow()
                .price();
    }

    static List<BigDecimal> amountsOf(List<MenuSectionResponse> sections, String itemId) {
        MenuPrice price = priceOf(sections, itemId);
        return price == null ? List.of() : price.options().stream().map(PriceOption::amount).toList();
    }
}

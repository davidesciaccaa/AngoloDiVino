package com.angolodivino.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class MenuPriceTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void readsEverySupportedLegacySinglePrice() throws Exception {
        assertAmounts(read("25"), "25");
        assertAmounts(read("\"25\""), "25");
        assertAmounts(read("\"2,50\""), "2.5");
        assertAmounts(read("\"25 €\""), "25");
        assertAmounts(read("\"25 \u00e2\u201a\u00ac\""), "25");
    }

    @Test
    void readsAbsentAndMultipleLegacyPricesWithoutTruncation() throws Exception {
        assertThat(read("\"-\"")).isNull();
        assertAmounts(read("\"5 € / 22 €\""), "5", "22");
        assertAmounts(read("\"6 € - 24 €\""), "6", "24");
        assertAmounts(read("[5,22]"), "5", "22");
    }

    @Test
    void wineSectionAddsConfirmedGlassAndBottleLabels() throws Exception {
        MenuItemResponse item = new MenuItemResponse("wine", "Wine", "", "", List.of(),
                read("\"5 € / 22 €\""));
        MenuSectionResponse section = new MenuSectionResponse("vini", "Vini", "", List.of(item));

        assertThat(section.items().get(0).price().options())
                .extracting(PriceOption::label)
                .containsExactly(PriceOptionLabel.GLASS, PriceOptionLabel.BOTTLE);
    }

    @Test
    void rejectsNegativeNonFiniteAmbiguousAndMalformedValues() {
        for (String json : List.of("-1", "\"-1\"", "\"5 circa\"", "\"5 / x\"", "[]",
                "{\"options\":[{\"amount\":\"5\"}]}",
                "{\"options\":[{\"label\":\"glass\",\"amount\":5},{\"label\":\"glass\",\"amount\":22}]}")) {
            assertThatThrownBy(() -> read(json))
                    .as("JSON %s", json)
                    .isInstanceOf(JsonMappingException.class);
        }
        assertThatThrownBy(() -> read("NaN")).isInstanceOf(java.io.IOException.class);
    }

    @Test
    void serializesOnlyNullOrStructuredNumericOptions() throws Exception {
        MenuPrice multiple = new MenuPrice(List.of(
                new PriceOption(PriceOptionLabel.GLASS, new BigDecimal("5")),
                new PriceOption(PriceOptionLabel.BOTTLE, new BigDecimal("22"))));
        MenuItemResponse item = new MenuItemResponse("wine", "Wine", "", "", List.of(), multiple);
        JsonNode json = mapper.readTree(mapper.writeValueAsBytes(item));

        assertThat(json.get("price").get("options").get(0).get("amount").isNumber()).isTrue();
        assertThat(json.get("price").get("options").get(0).get("label").asText()).isEqualTo("glass");
        assertThat(json.toString()).doesNotContain("€", "\u00e2\u201a\u00ac");

        MenuItemResponse absent = new MenuItemResponse("none", "None", "", "", List.of(), null);
        assertThat(mapper.readTree(mapper.writeValueAsBytes(absent)).get("price").isNull()).isTrue();
    }

    private MenuPrice read(String json) throws Exception {
        return mapper.readValue(json, MenuPrice.class);
    }

    private static void assertAmounts(MenuPrice price, String... expected) {
        assertThat(price.options()).extracting(PriceOption::amount)
                .containsExactly(java.util.Arrays.stream(expected).map(BigDecimal::new).toArray(BigDecimal[]::new));
    }
}

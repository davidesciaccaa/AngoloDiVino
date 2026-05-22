package com.angolodivino.menu;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CocktailServiceTest {

    private final CocktailService cocktailService = new CocktailService();

    @Test
    void returnsSignatureCocktails() {
        assertThat(cocktailService.findSignatureCocktails())
                .hasSize(3)
                .allSatisfy(cocktail -> {
                    assertThat(cocktail.name()).isNotBlank();
                    assertThat(cocktail.ingredients()).isNotEmpty();
                    assertThat(cocktail.price()).endsWith("EUR");
                });
    }
}

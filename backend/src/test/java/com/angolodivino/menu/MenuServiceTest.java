package com.angolodivino.menu;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MenuServiceTest {

    private final MenuService menuService = new MenuService();

    @Test
    void returnsMenuSectionsWithTwoItemsEach() {
        assertThat(menuService.findMenuSections())
                .extracting(MenuSectionResponse::id)
                .containsExactly("aperitivo", "drink", "vini", "superalcolici", "bevande");

        assertThat(menuService.findMenuSections())
                .allSatisfy(section -> {
                    assertThat(section.items()).hasSize(2);
                    assertThat(section.items()).allSatisfy(item -> {
                        assertThat(item.name()).isNotBlank();
                        assertThat(item.notes()).isNotEmpty();
                        assertThat(item.price()).endsWith("EUR");
                    });
                });
    }
}

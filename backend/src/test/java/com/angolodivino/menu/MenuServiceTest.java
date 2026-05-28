package com.angolodivino.menu;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class MenuServiceTest {

    private final MenuService menuService = new MenuService();

    @Test
    void returnsMenuSectionsInCorrectOrder() {
        List<MenuSectionResponse> sections = menuService.findMenuSections();
        assertThat(sections)
                .extracting(MenuSectionResponse::id)
                .containsExactly("aperitivo", "drink", "vini", "amari", "superalcolici", "frullati", "bevande");
    }

    @Test
    void allSectionsArePopulated() {
        List<MenuSectionResponse> sections = menuService.findMenuSections();
        
        sections.forEach(section -> {
            assertThat(section.items()).isNotEmpty();
            assertThat(section.items()).allSatisfy(item -> {
                assertThat(item.name()).isNotBlank();
            });
        });
    }
}

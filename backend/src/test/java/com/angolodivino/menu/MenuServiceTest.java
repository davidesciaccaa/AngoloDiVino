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
                .containsExactly("aperitivo", "drink", "vini", "frullati", "superalcolici", "bevande");
    }

    @Test
    void aperitivoSectionIsPopulated() {
        MenuSectionResponse aperitivo = menuService.findMenuSections().stream()
                .filter(s -> "aperitivo".equals(s.id()))
                .findFirst()
                .orElseThrow();
        
        assertThat(aperitivo.items()).isNotEmpty();
    }

    @Test
    void otherSectionsArePopulated() {
        List<MenuSectionResponse> sections = menuService.findMenuSections();
        
        sections.forEach(section -> {
            assertThat(section.items()).isNotEmpty();
            assertThat(section.items()).allSatisfy(item -> {
                assertThat(item.name()).isNotBlank();
            });
        });
    }
}

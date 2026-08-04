package com.angolodivino.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

public record MenuItemResponse(String id, String name, String subtitle, String description,
        List<String> notes,
        @JsonInclude(JsonInclude.Include.ALWAYS) MenuPrice price,
        Map<String, LocalizedMenuItemText> translations) {

    public MenuItemResponse {
        notes = notes == null ? null : List.copyOf(notes);
        translations = translations == null ? Map.of() : Map.copyOf(translations);
    }

    public MenuItemResponse(String id, String name, String subtitle, String description,
            List<String> notes, MenuPrice price) {
        this(id, name, subtitle, description, notes, price, Map.of());
    }

    public MenuItemResponse withPrice(MenuPrice replacement) {
        return new MenuItemResponse(id, name, subtitle, description, notes, replacement, translations);
    }
}

package com.angolodivino.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

public record MenuItemResponse(String id, String name, String subtitle, String description,
        List<String> notes,
        @JsonInclude(JsonInclude.Include.ALWAYS) MenuPrice price) {

    public MenuItemResponse {
        notes = notes == null ? null : List.copyOf(notes);
    }

    public MenuItemResponse withPrice(MenuPrice replacement) {
        return new MenuItemResponse(id, name, subtitle, description, notes, replacement);
    }
}

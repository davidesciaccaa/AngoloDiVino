package com.angolodivino.menu;

import jakarta.validation.constraints.Size;
import java.util.List;

/** Localized, editable text stored together with its Italian menu item. */
public record LocalizedMenuItemText(
        @Size(max = 120) String name,
        @Size(max = 80) String subtitle,
        @Size(max = 1000) String description,
        @Size(max = 30) List<@Size(max = 120) String> notes) {

    public LocalizedMenuItemText {
        notes = notes == null ? null : List.copyOf(notes);
    }
}

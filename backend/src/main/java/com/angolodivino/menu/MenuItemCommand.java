package com.angolodivino.menu;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/** API input keeps currency out of the payload and supports one or more numeric price options. */
public record MenuItemCommand(
        @NotBlank @Size(max = 80) String sectionId,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 80) String subtitle,
        @Size(max = 1000) String description,
        @Size(max = 30) List<@Size(max = 120) String> notes,
        @JsonDeserialize(using = AdminMenuPriceDeserializer.class) MenuPrice price,
        Map<String, @Valid LocalizedMenuItemText> translations,
        Boolean autoTranslate) {

    public MenuItemCommand(String sectionId, String name, String subtitle, String description,
            List<String> notes, MenuPrice price) {
        this(sectionId, name, subtitle, description, notes, price, null, false);
    }
}

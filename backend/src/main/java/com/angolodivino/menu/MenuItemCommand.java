package com.angolodivino.menu;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/** API input keeps currency out of the payload and supports one or more numeric price options. */
public record MenuItemCommand(
        @NotBlank @Size(max = 80) String sectionId,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 80) String subtitle,
        @Size(max = 1000) String description,
        @Size(max = 30) List<@Size(max = 120) String> notes,
        @JsonDeserialize(using = AdminMenuPriceDeserializer.class) MenuPrice price) { }

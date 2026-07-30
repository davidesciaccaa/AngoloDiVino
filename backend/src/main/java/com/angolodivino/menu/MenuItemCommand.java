package com.angolodivino.menu;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/** API input keeps currency out of the payload: price is a JSON number. */
public record MenuItemCommand(
        @NotBlank @Size(max = 80) String sectionId,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 80) String subtitle,
        @Size(max = 1000) String description,
        @Size(max = 30) List<@Size(max = 120) String> notes,
        @NotNull @DecimalMin("0.01") @DecimalMax("9999.99") BigDecimal price) { }

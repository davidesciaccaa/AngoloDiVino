package com.angolodivino.menu;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.menu")
public class MenuProperties {

    /**
     * JSON file holding the price overrides applied on top of the hardcoded menu.
     * When the file is missing, the hardcoded prices in {@link MenuService} are used.
     */
    @NotBlank
    private String overridesFile = "data/menu-overrides.json";

    public String getOverridesFile() {
        return overridesFile;
    }

    public void setOverridesFile(String overridesFile) {
        this.overridesFile = overridesFile;
    }
}

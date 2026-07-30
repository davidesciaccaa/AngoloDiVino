package com.angolodivino.menu;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.menu")
public class MenuProperties {

    /** Relative paths are resolved from the backend process working directory. */
    @NotBlank
    private String dataDirectory = "data";

    @NotBlank
    private String defaultResource = "classpath:menu.default.json";

    /** Optional location of the previous price-only file, used once during migration. */
    private String legacyOverridesFile = "";

    @Min(1)
    private int dailyBackupRetention = 30;

    @Min(12)
    private int monthlyBackupRetention = 12;

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public String getDefaultResource() {
        return defaultResource;
    }

    public void setDefaultResource(String defaultResource) {
        this.defaultResource = defaultResource;
    }

    public String getLegacyOverridesFile() {
        return legacyOverridesFile;
    }

    public void setLegacyOverridesFile(String legacyOverridesFile) {
        this.legacyOverridesFile = legacyOverridesFile;
    }

    public int getDailyBackupRetention() {
        return dailyBackupRetention;
    }

    public void setDailyBackupRetention(int dailyBackupRetention) {
        this.dailyBackupRetention = dailyBackupRetention;
    }

    public int getMonthlyBackupRetention() {
        return monthlyBackupRetention;
    }

    public void setMonthlyBackupRetention(int monthlyBackupRetention) {
        this.monthlyBackupRetention = monthlyBackupRetention;
    }
}

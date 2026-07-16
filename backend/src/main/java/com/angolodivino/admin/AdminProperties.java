package com.angolodivino.admin;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.admin")
public class AdminProperties {

    /**
     * Password for the admin panel, supplied via the ADMIN_PASSWORD environment variable.
     * Blank disables the admin API entirely.
     */
    private String password = "";

    /** How long an issued admin token stays valid. */
    @NotNull
    private Duration sessionTtl = Duration.ofHours(8);

    public boolean isEnabled() {
        return password != null && !password.isBlank();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Duration getSessionTtl() {
        return sessionTtl;
    }

    public void setSessionTtl(Duration sessionTtl) {
        this.sessionTtl = sessionTtl;
    }
}

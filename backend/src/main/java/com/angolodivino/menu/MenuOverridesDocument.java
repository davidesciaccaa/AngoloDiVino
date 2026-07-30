package com.angolodivino.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Persisted menu snapshot. {@code prices} is retained only to migrate the previous file format. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MenuOverridesDocument(Instant updatedAt, List<MenuSectionResponse> sections,
        Map<String, String> prices) {
}

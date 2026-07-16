package com.angolodivino.admin;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record UpdatePricesRequest(
        @NotEmpty @Size(max = 500) Map<String, String> prices
) {
}

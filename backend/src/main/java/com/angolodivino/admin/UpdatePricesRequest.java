package com.angolodivino.admin;

import com.angolodivino.menu.MenuPrice;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record UpdatePricesRequest(
        @NotEmpty @Size(max = 500) Map<String, MenuPrice> prices
) {
}

package com.angolodivino.admin;

import com.angolodivino.menu.AdminMenuPriceDeserializer;
import com.angolodivino.menu.MenuPrice;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record UpdatePricesRequest(
        @NotEmpty @Size(max = 500)
        @JsonDeserialize(contentUsing = AdminMenuPriceDeserializer.class)
        Map<String, MenuPrice> prices
) {
}

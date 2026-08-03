package com.angolodivino.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

public record PriceOption(
        @JsonInclude(JsonInclude.Include.NON_NULL) PriceOptionLabel label,
        BigDecimal amount) {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999.99");

    public PriceOption {
        if (amount == null) {
            throw new IllegalArgumentException("L'importo del prezzo è obbligatorio");
        }
        BigDecimal normalized = amount.stripTrailingZeros();
        if (normalized.scale() < 0) {
            normalized = normalized.setScale(0);
        }
        if (normalized.scale() > 2
                || normalized.signum() <= 0
                || normalized.compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException("Importo prezzo non valido: " + amount);
        }
        amount = normalized;
    }
}

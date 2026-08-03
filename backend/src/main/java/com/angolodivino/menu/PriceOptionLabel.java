package com.angolodivino.menu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PriceOptionLabel {
    GLASS("glass"),
    BOTTLE("bottle");

    private final String jsonValue;

    PriceOptionLabel(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String jsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static PriceOptionLabel fromJson(String value) {
        for (PriceOptionLabel label : values()) {
            if (label.jsonValue.equals(value)) {
                return label;
            }
        }
        throw new IllegalArgumentException("Etichetta prezzo non valida: " + value);
    }
}

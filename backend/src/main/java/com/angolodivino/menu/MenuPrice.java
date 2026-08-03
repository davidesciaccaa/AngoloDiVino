package com.angolodivino.menu;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record MenuPrice(List<PriceOption> options) {

    private static final int MAX_OPTIONS = 8;

    public MenuPrice {
        if (options == null || options.isEmpty() || options.size() > MAX_OPTIONS) {
            throw new IllegalArgumentException("Il prezzo deve contenere da 1 a " + MAX_OPTIONS + " importi");
        }
        options = List.copyOf(options);
        Set<PriceOptionLabel> labels = new HashSet<>();
        for (PriceOption option : options) {
            if (option == null) {
                throw new IllegalArgumentException("Opzione prezzo non valida");
            }
            if (option.label() != null && !labels.add(option.label())) {
                throw new IllegalArgumentException("Etichetta prezzo duplicata: " + option.label().jsonValue());
            }
        }
    }

    public static MenuPrice single(BigDecimal amount) {
        return new MenuPrice(List.of(new PriceOption(null, amount)));
    }

    public MenuPrice withLabelsForSection(String sectionId) {
        boolean hasLabels = options.stream().anyMatch(option -> option.label() != null);
        if (!"vini".equals(sectionId)) {
            if (hasLabels) {
                throw new IllegalArgumentException("Le etichette glass/bottle sono valide solo per i vini");
            }
            return this;
        }
        if (options.size() != 2) {
            if (hasLabels) {
                throw new IllegalArgumentException("I prezzi etichettati dei vini richiedono calice e bottiglia");
            }
            return this;
        }
        if (!hasLabels) {
            return new MenuPrice(List.of(
                    new PriceOption(PriceOptionLabel.GLASS, options.get(0).amount()),
                    new PriceOption(PriceOptionLabel.BOTTLE, options.get(1).amount())));
        }
        if (options.get(0).label() != PriceOptionLabel.GLASS
                || options.get(1).label() != PriceOptionLabel.BOTTLE) {
            throw new IllegalArgumentException("L'ordine dei prezzi dei vini deve essere glass, bottle");
        }
        return this;
    }
}

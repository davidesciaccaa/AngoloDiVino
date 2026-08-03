package com.angolodivino.menu;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MenuService {
    private final MenuOverridesStore store;

    public MenuService(MenuOverridesStore store) {
        this.store = store;
    }

    public List<MenuSectionResponse> findMenuSections() {
        return store.readMenu();
    }

    public List<MenuItemResponse> findSignatureDrinks() {
        return findMenuSections().stream()
                .filter(section -> "drink".equals(section.id()))
                .findFirst()
                .map(MenuSectionResponse::items)
                .orElseGet(List::of);
    }

    /**
     * Retains the previous price-only endpoint while persisting the complete runtime menu.
     */
    public List<MenuSectionResponse> updatePrices(Map<String, MenuPrice> requestedPrices) {
        if (requestedPrices == null || requestedPrices.isEmpty()) {
            throw new IllegalArgumentException("Specificare almeno un prezzo");
        }

        return store.updateMenu(sections -> {
            Map<String, MenuItemResponse> itemsById = sections.stream()
                    .flatMap(section -> section.items().stream())
                    .collect(Collectors.toMap(
                            MenuItemResponse::id,
                            item -> item,
                            (first, second) -> first,
                            LinkedHashMap::new));
            Set<String> unknownIds = requestedPrices.keySet().stream()
                    .filter(id -> id == null || !itemsById.containsKey(id))
                    .map(String::valueOf)
                    .collect(Collectors.toCollection(TreeSet::new));
            if (!unknownIds.isEmpty()) {
                throw new IllegalArgumentException("Voci di menù sconosciute: " + String.join(", ", unknownIds));
            }

            List<MenuSectionResponse> menu = new ArrayList<>(sections);
            for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex++) {
                MenuSectionResponse section = sections.get(sectionIndex);
                List<MenuItemResponse> updatedItems = null;
                for (int itemIndex = 0; itemIndex < section.items().size(); itemIndex++) {
                    MenuItemResponse item = section.items().get(itemIndex);
                    if (!requestedPrices.containsKey(item.id())) {
                        continue;
                    }
                    if (updatedItems == null) {
                        updatedItems = new ArrayList<>(section.items());
                    }
                    updatedItems.set(itemIndex, new MenuItemResponse(
                            item.id(),
                            item.name(),
                            item.subtitle(),
                            item.description(),
                            item.notes(),
                            priceForSection(requestedPrices.get(item.id()), section.id())));
                }
                if (updatedItems != null) {
                    menu.set(sectionIndex, new MenuSectionResponse(
                            section.id(), section.title(), section.description(), updatedItems));
                }
            }
            return menu;
        });
    }

    private static MenuPrice priceForSection(MenuPrice price, String sectionId) {
        return price == null ? null : price.withLabelsForSection(sectionId);
    }
}

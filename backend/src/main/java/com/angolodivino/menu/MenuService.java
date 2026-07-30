package com.angolodivino.menu;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MenuService {
    private static final Pattern PRICE_PATTERN = Pattern.compile("^[0-9 ,./€-]{1,32}$");
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
    public List<MenuSectionResponse> updatePrices(Map<String, String> requestedPrices) {
        Set<String> invalidPrices = requestedPrices.entrySet().stream()
                .filter(entry -> entry.getValue() == null
                        || !PRICE_PATTERN.matcher(entry.getValue().trim()).matches())
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(TreeSet::new));
        if (!invalidPrices.isEmpty()) {
            throw new IllegalArgumentException("Prezzi non validi per: " + String.join(", ", invalidPrices));
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

            return sections.stream()
                    .map(section -> new MenuSectionResponse(
                            section.id(),
                            section.title(),
                            section.description(),
                            section.items().stream()
                                    .map(item -> requestedPrices.containsKey(item.id())
                                            ? new MenuItemResponse(
                                                    item.id(),
                                                    item.name(),
                                                    item.subtitle(),
                                                    item.description(),
                                                    item.notes(),
                                                    requestedPrices.get(item.id()).trim())
                                            : item)
                                    .toList()))
                    .toList();
        });
    }
}

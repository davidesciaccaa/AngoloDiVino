package com.angolodivino.menu;

import java.util.List;

public record MenuSectionResponse(
        String id,
        String title,
        String description,
        List<MenuItemResponse> items
) {
    public MenuSectionResponse {
        if (items != null) {
            items = items.stream()
                    .map(item -> item != null && item.price() != null
                            ? item.withPrice(item.price().withLabelsForSection(id))
                            : item)
                    .toList();
        }
    }
}

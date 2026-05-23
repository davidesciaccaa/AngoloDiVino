package com.angolodivino.menu;

import java.util.List;

public record MenuSectionResponse(
        String id,
        String title,
        String description,
        List<MenuItemResponse> items
) {
}

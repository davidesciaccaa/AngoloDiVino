package com.angolodivino.menu;

import java.util.List;

public record MenuItemResponse(
        String name,
        String subtitle,
        String description,
        List<String> notes,
        String price
) {
}

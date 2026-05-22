package com.angolodivino.menu;

import java.util.List;

public record CocktailResponse(
        String name,
        String subtitle,
        String description,
        List<String> ingredients,
        String price
) {
}

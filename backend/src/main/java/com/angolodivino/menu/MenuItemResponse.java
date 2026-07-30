package com.angolodivino.menu;

import java.util.List;

public record MenuItemResponse(String id, String name, String subtitle, String description,
        List<String> notes, String price) { }

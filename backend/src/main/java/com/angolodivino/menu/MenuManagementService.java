package com.angolodivino.menu;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MenuManagementService {
    private final MenuOverridesStore store;

    public MenuManagementService(MenuOverridesStore store) {
        this.store = store;
    }

    public List<MenuSectionResponse> create(MenuItemCommand command) {
        return store.updateMenu(current -> {
            List<MenuSectionResponse> menu = normalizeAndCopy(current);
            MenuSectionResponse section = section(menu, command.sectionId());
            List<MenuItemResponse> items = new ArrayList<>(section.items());
            items.add(toItem(uniqueId(menu, command.name()), command));
            replace(menu, new MenuSectionResponse(section.id(), section.title(), section.description(), items));
            return menu;
        });
    }

    public List<MenuSectionResponse> update(String id, MenuItemCommand command) {
        return store.updateMenu(current -> {
            List<MenuSectionResponse> menu = normalizeAndCopy(current);
            if (find(menu, id) == null) {
                throw new IllegalArgumentException("Voce di menù sconosciuta: " + id);
            }
            for (int index = 0; index < menu.size(); index++) {
                MenuSectionResponse section = menu.get(index);
                List<MenuItemResponse> items = new ArrayList<>(section.items());
                if (items.removeIf(item -> item.id().equals(id))) {
                    menu.set(index, new MenuSectionResponse(
                            section.id(), section.title(), section.description(), items));
                    break;
                }
            }
            MenuSectionResponse destination = section(menu, command.sectionId());
            List<MenuItemResponse> items = new ArrayList<>(destination.items());
            items.add(toItem(id, command));
            replace(menu, new MenuSectionResponse(
                    destination.id(), destination.title(), destination.description(), items));
            return menu;
        });
    }

    public List<MenuSectionResponse> delete(String id) {
        return store.updateMenu(current -> {
            List<MenuSectionResponse> menu = normalizeAndCopy(current);
            for (int index = 0; index < menu.size(); index++) {
                MenuSectionResponse section = menu.get(index);
                List<MenuItemResponse> items = new ArrayList<>(section.items());
                if (items.removeIf(item -> item.id().equals(id))) {
                    menu.set(index, new MenuSectionResponse(
                            section.id(), section.title(), section.description(), items));
                    return menu;
                }
            }
            throw new IllegalArgumentException("Voce di menù sconosciuta: " + id);
        });
    }

    private static List<MenuSectionResponse> normalizeAndCopy(List<MenuSectionResponse> current) {
        return current.stream()
                .map(section -> new MenuSectionResponse(
                        section.id(),
                        section.title(),
                        section.description(),
                        section.items().stream()
                                .map(item -> new MenuItemResponse(
                                        item.id(),
                                        item.name(),
                                        item.subtitle(),
                                        item.description(),
                                        item.notes(),
                                        numericPrice(item.price())))
                                .toList()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    private static String numericPrice(String price) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("\\d+(?:[,.]\\d+)?")
                .matcher(price == null ? "" : price);
        return matcher.find() ? matcher.group().replace(',', '.') : "0";
    }

    private static MenuItemResponse toItem(String id, MenuItemCommand command) {
        return new MenuItemResponse(
                id,
                command.name().trim(),
                text(command.subtitle()),
                text(command.description()),
                command.notes() == null
                        ? List.of()
                        : command.notes().stream()
                                .map(String::trim)
                                .filter(note -> !note.isEmpty())
                                .toList(),
                command.price().stripTrailingZeros().toPlainString());
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }

    private static MenuSectionResponse section(List<MenuSectionResponse> menu, String id) {
        return menu.stream()
                .filter(section -> section.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Categoria sconosciuta: " + id));
    }

    private static MenuItemResponse find(List<MenuSectionResponse> menu, String id) {
        return menu.stream()
                .flatMap(section -> section.items().stream())
                .filter(item -> item.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    private static void replace(List<MenuSectionResponse> menu, MenuSectionResponse replacement) {
        for (int index = 0; index < menu.size(); index++) {
            if (menu.get(index).id().equals(replacement.id())) {
                menu.set(index, replacement);
                return;
            }
        }
    }

    private static String uniqueId(List<MenuSectionResponse> menu, String name) {
        String base = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("(^_|_$)", "");
        if (base.isBlank()) {
            base = "piatto";
        }
        String id = base;
        for (int suffix = 2; find(menu, id) != null; suffix++) {
            id = base + "_" + suffix;
        }
        return id;
    }
}

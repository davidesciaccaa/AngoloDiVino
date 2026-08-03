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
            List<MenuSectionResponse> menu = new ArrayList<>(current);
            int sectionIndex = sectionIndex(menu, command.sectionId());
            MenuSectionResponse section = menu.get(sectionIndex);
            List<MenuItemResponse> items = new ArrayList<>(section.items());
            items.add(toItem(uniqueId(menu, command.name()), command));
            menu.set(sectionIndex,
                    new MenuSectionResponse(section.id(), section.title(), section.description(), items));
            return menu;
        });
    }

    public List<MenuSectionResponse> update(String id, MenuItemCommand command) {
        return store.updateMenu(current -> {
            int sourceSectionIndex = -1;
            int sourceItemIndex = -1;
            for (int sectionIndex = 0; sectionIndex < current.size(); sectionIndex++) {
                List<MenuItemResponse> items = current.get(sectionIndex).items();
                for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                    if (items.get(itemIndex).id().equals(id)) {
                        sourceSectionIndex = sectionIndex;
                        sourceItemIndex = itemIndex;
                        break;
                    }
                }
                if (sourceSectionIndex >= 0) {
                    break;
                }
            }
            if (sourceSectionIndex < 0) {
                throw new IllegalArgumentException("Voce di menù sconosciuta: " + id);
            }
            int destinationSectionIndex = sectionIndex(current, command.sectionId());
            List<MenuSectionResponse> menu = new ArrayList<>(current);
            MenuItemResponse replacement = toItem(id, command);

            if (sourceSectionIndex == destinationSectionIndex) {
                MenuSectionResponse section = current.get(sourceSectionIndex);
                List<MenuItemResponse> items = new ArrayList<>(section.items());
                items.set(sourceItemIndex, replacement);
                menu.set(sourceSectionIndex,
                        new MenuSectionResponse(section.id(), section.title(), section.description(), items));
                return menu;
            }

            MenuSectionResponse source = current.get(sourceSectionIndex);
            List<MenuItemResponse> sourceItems = new ArrayList<>(source.items());
            sourceItems.remove(sourceItemIndex);
            menu.set(sourceSectionIndex,
                    new MenuSectionResponse(source.id(), source.title(), source.description(), sourceItems));

            MenuSectionResponse destination = current.get(destinationSectionIndex);
            List<MenuItemResponse> destinationItems = new ArrayList<>(destination.items());
            destinationItems.add(replacement);
            menu.set(destinationSectionIndex, new MenuSectionResponse(
                    destination.id(), destination.title(), destination.description(), destinationItems));
            return menu;
        });
    }

    public List<MenuSectionResponse> delete(String id) {
        return store.updateMenu(current -> {
            for (int index = 0; index < current.size(); index++) {
                MenuSectionResponse section = current.get(index);
                List<MenuItemResponse> items = new ArrayList<>(section.items());
                if (items.removeIf(item -> item.id().equals(id))) {
                    List<MenuSectionResponse> menu = new ArrayList<>(current);
                    menu.set(index, new MenuSectionResponse(
                            section.id(), section.title(), section.description(), items));
                    return menu;
                }
            }
            throw new IllegalArgumentException("Voce di menù sconosciuta: " + id);
        });
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
                command.price() == null
                        ? null
                        : command.price().withLabelsForSection(command.sectionId()));
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }

    private static int sectionIndex(List<MenuSectionResponse> menu, String id) {
        for (int index = 0; index < menu.size(); index++) {
            if (menu.get(index).id().equals(id)) {
                return index;
            }
        }
        throw new IllegalArgumentException("Categoria sconosciuta: " + id);
    }

    private static MenuItemResponse find(List<MenuSectionResponse> menu, String id) {
        return menu.stream()
                .flatMap(section -> section.items().stream())
                .filter(item -> item.id().equals(id))
                .findFirst()
                .orElse(null);
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

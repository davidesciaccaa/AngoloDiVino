package com.angolodivino.menu;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MenuManagementService {
    private final MenuService menuService;
    private final MenuOverridesStore store;
    public MenuManagementService(MenuService menuService, MenuOverridesStore store) { this.menuService = menuService; this.store = store; }

    public synchronized List<MenuSectionResponse> create(MenuItemCommand command) {
        List<MenuSectionResponse> menu = mutable(); MenuSectionResponse section = section(menu, command.sectionId());
        List<MenuItemResponse> items = new ArrayList<>(section.items()); items.add(toItem(uniqueId(menu, command.name()), command));
        replace(menu, new MenuSectionResponse(section.id(), section.title(), section.description(), items)); return save(menu);
    }
    public synchronized List<MenuSectionResponse> update(String id, MenuItemCommand command) {
        List<MenuSectionResponse> menu = mutable(); MenuItemResponse old = find(menu, id);
        if (old == null) throw new IllegalArgumentException("Voce di menù sconosciuta: " + id);
        for (int n = 0; n < menu.size(); n++) { MenuSectionResponse s = menu.get(n); List<MenuItemResponse> items = new ArrayList<>(s.items()); if (items.removeIf(i -> i.id().equals(id))) { menu.set(n, new MenuSectionResponse(s.id(), s.title(), s.description(), items)); break; } }
        MenuSectionResponse destination = section(menu, command.sectionId()); List<MenuItemResponse> items = new ArrayList<>(destination.items()); items.add(toItem(id, command));
        replace(menu, new MenuSectionResponse(destination.id(), destination.title(), destination.description(), items)); return save(menu);
    }
    public synchronized List<MenuSectionResponse> delete(String id) {
        List<MenuSectionResponse> menu = mutable();
        for (int n = 0; n < menu.size(); n++) { MenuSectionResponse s = menu.get(n); List<MenuItemResponse> items = new ArrayList<>(s.items()); if (items.removeIf(i -> i.id().equals(id))) { menu.set(n, new MenuSectionResponse(s.id(), s.title(), s.description(), items)); return save(menu); } }
        throw new IllegalArgumentException("Voce di menù sconosciuta: " + id);
    }
    /** Converts legacy labels such as "5 € / 22 €" to their editable numeric price on the first save. */
    private List<MenuSectionResponse> mutable() { return store.readMenu(menuService.defaultMenuSections()).stream()
            .map(s -> new MenuSectionResponse(s.id(), s.title(), s.description(), s.items().stream()
                    .map(i -> new MenuItemResponse(i.id(), i.name(), i.subtitle(), i.description(), i.notes(), numericPrice(i.price())))
                    .toList())).collect(java.util.stream.Collectors.toCollection(ArrayList::new)); }
    private static String numericPrice(String price) { java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\d+(?:[,.]\\d+)?").matcher(price == null ? "" : price); return matcher.find() ? matcher.group().replace(',', '.') : "0"; }
    private List<MenuSectionResponse> save(List<MenuSectionResponse> menu) { store.writeMenu(menu); return menu; }
    private static MenuItemResponse toItem(String id, MenuItemCommand c) { return new MenuItemResponse(id, c.name().trim(), text(c.subtitle()), text(c.description()), c.notes() == null ? List.of() : c.notes().stream().map(String::trim).filter(s -> !s.isEmpty()).toList(), c.price().stripTrailingZeros().toPlainString()); }
    private static String text(String s) { return s == null ? "" : s.trim(); }
    private static MenuSectionResponse section(List<MenuSectionResponse> menu, String id) { return menu.stream().filter(s -> s.id().equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Categoria sconosciuta: " + id)); }
    private static MenuItemResponse find(List<MenuSectionResponse> menu, String id) { return menu.stream().flatMap(s -> s.items().stream()).filter(i -> i.id().equals(id)).findFirst().orElse(null); }
    private static void replace(List<MenuSectionResponse> menu, MenuSectionResponse section) { for (int n = 0; n < menu.size(); n++) if (menu.get(n).id().equals(section.id())) { menu.set(n, section); return; } }
    private static String uniqueId(List<MenuSectionResponse> menu, String name) { String base = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("(^_|_$)", ""); if (base.isBlank()) base = "piatto"; String id = base; for (int n = 2; find(menu, id) != null; n++) id = base + "_" + n; return id; }
}

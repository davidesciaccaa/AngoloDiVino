package com.angolodivino.menu;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    public List<MenuSectionResponse> findMenuSections() {
        return List.of(
                new MenuSectionResponse(
                        "aperitivo",
                        "Aperitivo",
                        "Assaggi pensati per aprire la serata con calma.",
                        List.of(
                                new MenuItemResponse(
                                        "Tagliere Salandra",
                                        "Formaggi, conserve",
                                        "Selezione di formaggi locali, olive, focaccia calda e confettura della casa.",
                                        List.of("Vegetariano", "Perfetto per due"),
                                        "14 EUR"
                                ),
                                new MenuItemResponse(
                                        "Fritti di Corte",
                                        "Croccanti, mediterranei",
                                        "Piccoli fritti misti con verdure di stagione, agrumi e maionese alle erbe.",
                                        List.of("Stuzzicheria", "Servito caldo"),
                                        "11 EUR"
                                )
                        )
                ),
                new MenuSectionResponse(
                        "drink",
                        "Drink",
                        "Semplice e diretto: Drink.",
                        List.of(
                                new MenuItemResponse(
                                        "Rubino Sour",
                                        "Vino rosso, agrumi",
                                        "Un sour vellutato con vino rosso ridotto, limone fresco e albume.",
                                        List.of("Signature", "Agrumato"),
                                        "12 EUR"
                                ),
                                new MenuItemResponse(
                                        "Notturno Bianco",
                                        "Gin, uva bianca",
                                        "Gin floreale, mosto d'uva bianca e una chiusura fresca di salvia.",
                                        List.of("Floreale", "Fresco"),
                                        "13 EUR"
                                )
                        )
                ),
                new MenuSectionResponse(
                        "vini",
                        "Vini",
                        "Etichette salentine e vini locali aperti da scoprire al calice.",
                        List.of(
                                new MenuItemResponse(
                                        "Primitivo del Cortile",
                                        "Rosso, Salento",
                                        "Calice morbido e speziato, ideale con assaggi sapidi e formaggi stagionati.",
                                        List.of("Calice", "Corposo"),
                                        "7 EUR"
                                ),
                                new MenuItemResponse(
                                        "Bianco di Pietra",
                                        "Bianco, Nardò",
                                        "Bianco minerale, teso e luminoso, con finale di mandorla fresca.",
                                        List.of("Calice", "Minerale"),
                                        "6 EUR"
                                )
                        )
                ),
                new MenuSectionResponse(
                        "superalcolici",
                        "Superalcolici",
                        "Distillati selezionati per degustazioni.",
                        List.of(
                                new MenuItemResponse(
                                        "Amaro dei Dotti",
                                        "Erbe, radici",
                                        "Amaro intenso con note balsamiche, scorza d'arancia e finale persistente.",
                                        List.of("Dopocena", "Servito freddo"),
                                        "6 EUR"
                                ),
                                new MenuItemResponse(
                                        "Rum Riserva 8",
                                        "Morbido, speziato",
                                        "Rum ambrato con vaniglia, cacao e legno dolce.",
                                        List.of("Degustazione", "Liscio"),
                                        "9 EUR"
                                )
                        )
                ),
                new MenuSectionResponse(
                        "bevande",
                        "Bevande",
                        "Analcolici, soft drink e alternative leggere per ogni momento.",
                        List.of(
                                new MenuItemResponse(
                                        "Limonata",
                                        "Agrumi, erbe",
                                        "Limonata fresca, zest di limone.",
                                        List.of("Analcolico", "Rinfrescante"),
                                        "5 EUR"
                                ),
                                new MenuItemResponse(
                                        "Tonica",
                                        "Erbe, agrumi",
                                        "Tonica secca con rosmarino, pompelmo rosa e ghiaccio pieno.",
                                        List.of("Analcolico", "Dry"),
                                        "5 EUR"
                                )
                        )
                )
        );
    }

    public List<MenuItemResponse> findSignatureDrinks() {
        return findMenuSections().stream()
                .filter(section -> "drink".equals(section.id()))
                .findFirst()
                .map(MenuSectionResponse::items)
                .orElseGet(List::of);
    }
}

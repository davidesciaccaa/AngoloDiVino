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
                                        "tris",
                                        "Tris",
                                        "Assaggi locali",
                                        "Una piccola selezione di stuzzichini del giorno.",
                                        List.of(),
                                        "1 €"
                                ),
                                new MenuItemResponse(
                                        "tagliere_salandra",
                                        "Tagliere Salandra",
                                        "Formaggi, conserve",
                                        "Selezione di formaggi locali, olive, focaccia calda e confettura della casa.",
                                        List.of("Vegetariano", "Perfetto per due"),
                                        "14 €"
                                ),
                                new MenuItemResponse(
                                        "fritti_di_corte",
                                        "Fritti di Corte",
                                        "Croccanti, mediterranei",
                                        "Piccoli fritti misti con verdure di stagione, agrumi e maionese alle erbe.",
                                        List.of("Stuzzicheria", "Servito caldo"),
                                        "11 €"
                                ),
                                new MenuItemResponse(
                                        "ostriche",
                                        "Ostriche",
                                        "Specialità di mare",
                                        "Ostriche fresche servite con limone e pepe nero.",
                                        List.of("Fresco", "Mare"),
                                        "8 €"
                                )
                        )
                ),
                new MenuSectionResponse(
                        "drink",
                        "Cocktails",
                        "I grandi classici e le nostre proposte miscelate.",
                        List.of(
                                new MenuItemResponse("aperol_spritz", "Aperol Spritz", "", "(4 cl Aperol, 1 dl Prosecco, Sprite q.b., arancia)", List.of(), "6 €"),
                                new MenuItemResponse("campari_spritz", "Campari Spritz", "", "(4 cl Campari, 1 dl Prosecco, acqua tonica q.b.)", List.of(), "6 €"),
                                new MenuItemResponse("caipirinha", "Caipirinha", "", "(4 cl Cachaça, zucchero di canna, lime, ghiaccio tritato)", List.of(), "7 €"),
                                new MenuItemResponse("caipiroska_fragola", "Caipiroska alla Fragola", "", "", List.of(), "7 €"),
                                new MenuItemResponse("gin_tonic", "Gin Tonic", "", "(4 cl Gin, acqua tonica)", List.of(), "6 € - 20 €"),
                                new MenuItemResponse("hugo", "Hugo", "", "(2 cl succo di lime, 2 cl sciroppo di sambuco, 1 dl Prosecco, Sprite q.b., menta)", List.of(), ""),
                                new MenuItemResponse("moscow_mule", "Moscow Mule", "", "(1,5 cl succo di lime, 4 cl Vodka, Ginger Beer)", List.of(), ""),
                                new MenuItemResponse("london_mule", "London Mule", "", "(1,5 cl succo di lime, 4 cl Gin, Ginger Beer)", List.of(), ""),
                                new MenuItemResponse("long_island", "Long Island Iced Tea", "", "(3 cl succo di lime, 2 cl zucchero di canna, 1,5 cl Triple Sec, 1,5 cl Gin, 1,5 cl Vodka, 1,5 cl Rum, 1,5 cl Tequila, Cola)", List.of(), ""),
                                new MenuItemResponse("japan_iced_tea", "Japan Iced Tea", "", "(1,5 cl Vodka, 1,5 cl Rum, 1,5 cl Gin, 1,5 cl Midori, 6 cl Sweet & Sour al limone)", List.of(), ""),
                                new MenuItemResponse("mojito_scuro", "Mojito Scuro", "", "(zucchero di canna, lime, menta, rum scuro, acqua frizzante)", List.of(), ""),
                                new MenuItemResponse("negroni", "Negroni", "", "(3 cl Gin, 3 cl Campari, 3 cl Vermouth rosso, arancia)", List.of(), ""),
                                new MenuItemResponse("negroni_sbagliato", "Negroni Sbagliato", "", "(3 cl Prosecco, 3 cl Bitter, 3 cl Vermouth rosso)", List.of(), ""),
                                new MenuItemResponse("sex_on_the_beach", "Sex on the Beach", "", "(4 cl Vodka, 2 cl liquore alla pesca, 4 cl succo d’arancia, 4 cl succo di mirtillo)", List.of(), ""),
                                new MenuItemResponse("martini_cocktail", "Martini Cocktail", "", "(6 cl Gin, 1 cl Martini Dry, scorza di lime, olive)", List.of(), ""),
                                new MenuItemResponse("espresso_martini", "Espresso Martini", "", "(5 cl Vodka, 2 cl caffè espresso, liquore al caffè, zucchero)", List.of(), ""),
                                new MenuItemResponse("cosmopolitan", "Cosmopolitan", "", "(4 cl Vodka, 1,5 cl Triple Sec, 3 cl succo di mirtillo, 1,5 cl succo di lime, scorza d’arancia, ribes rosso)", List.of(), "")
                        )
                ),
                new MenuSectionResponse(
                        "vini",
                        "Vini",
                        "Una selezione ricercata di vini bianchi, rosati e rossi dalla Cantina Sampietrana e Verdeca.",
                        List.of(
                                // Bianchi
                                new MenuItemResponse("tacco_barocco_bianco", "Tacco Barocco - Negroamaro Bianco Primitivo", "Bianchi", "Note di fiori bianchi e agrumi, fresco e persistente. Cantina Sampietrana. 750ml", List.of(), "5 € / 22 €"),
                                new MenuItemResponse("verdeca_salento", "Verdeca del Salento", "", "Vino autoctono dal bouquet delicato e sapore secco. Cantina Verdeca. 750ml", List.of(), "5 € / 18 €"),
                                new MenuItemResponse("verdeca_itria", "Verdeca Valle d'Itria", "", "Fresco, fruttato e con una piacevole sapidità. Cantina Verdeca. 750ml", List.of(), "5 € / 18 €"),
                                // Rossi
                                new MenuItemResponse("tacco_barocco_negroamaro", "Tacco Barocco - Negroamaro", "Rossi", "Rosso rubino intenso con sentori di piccoli frutti rossi. Cantina Sampietrana. 750ml", List.of(), "5 € / 22 €"),
                                new MenuItemResponse("tacco_barocco_puglia_igp", "Tacco Barocco - Puglia IGP", "", "Corposo ed equilibrato, perfetto per accompagnare taglieri. Cantina Sampietrana. 750ml", List.of(), "6 € - 20 €"),
                                // Rosati
                                new MenuItemResponse("rosato_salento", "Rosato del Salento", "Rosati", "Fresco, fruttato, con note di ciliegia e lampone. 750ml", List.of(), "5 € / 18 €"),
                                // Bollicine
                                new MenuItemResponse("prosecco_doc", "Prosecco D.O.C.", "Bollicine", "Perlage fine e persistente, ideale come aperitivo. 750ml", List.of(), "4 € / 16 €")
                        )
                ),
                new MenuSectionResponse(
                        "amari",
                        "Amari",
                        "Selezione di amari e liquori per chiudere in bellezza.",
                        List.of(
                                new MenuItemResponse("ramazzotti", "Ramazzotti", "", "", List.of(), "4 €"),
                                new MenuItemResponse("cynar", "Cynar", "", "", List.of(), "4 €"),
                                new MenuItemResponse("montenegro", "Montenegro", "", "", List.of(), "4 €"),
                                new MenuItemResponse("fernet", "Fernet Branca Menta", "", "", List.of(), "4 €"),
                                new MenuItemResponse("averna", "Averna", "", "", List.of(), "4 €"),
                                new MenuItemResponse("petrus", "Petrus", "", "", List.of(), "4 €"),
                                new MenuItemResponse("jagermeister", "Jagermeister", "", "", List.of(), "4 €"),
                                new MenuItemResponse("jefferson", "Jefferson", "", "", List.of(), "4 €"),
                                new MenuItemResponse("unicum", "Unicum", "", "", List.of(), "4 €"),
                                new MenuItemResponse("lucano", "Lucano", "", "", List.of(), "4 €"),
                                new MenuItemResponse("del_capo", "Amaro del capo", "", "", List.of(), "4 €"),
                                new MenuItemResponse("sambuca", "Sambuca", "", "", List.of(), "4 €"),
                                new MenuItemResponse("borghetti", "Caffè borghetti", "", "", List.of(), "4 €"),
                                new MenuItemResponse("vena_caffe", "Vena Caffè", "", "", List.of(), "4 €")
                        )
                ),
                new MenuSectionResponse(
                        "superalcolici",
                        "Distillati e Rum",
                        "Una selezione di pregiati distillati e rum da meditazione.",
                        List.of(
                                // Distillati
                                new MenuItemResponse("vecchia_romagna", "Vecchia Romagna", "Distillato", "", List.of(), "5 €"),
                                new MenuItemResponse("cointreau", "Cointreau", "Distillato", "", List.of(), "5 €"),
                                new MenuItemResponse("jack_daniels", "Jack Daniel’s", "Distillato", "", List.of(), "6 €"),
                                new MenuItemResponse("jack_daniels_honey", "Jack Daniel’s Honey", "Distillato", "", List.of(), "6 €"),
                                new MenuItemResponse("cardinal_mendoza", "Cardinal Mendoza", "Distillato", "", List.of(), "12 €"),
                                new MenuItemResponse("oban", "Oban", "Distillato", "", List.of(), "12 €"),
                                new MenuItemResponse("laphroaig", "Laphroaig", "Distillato", "", List.of(), "12 €"),
                                new MenuItemResponse("lagavulin", "Lagavulin", "Distillato", "", List.of(), "12 €"),
                                new MenuItemResponse("martini", "Martini Bianco / Rosso / Dry", "Distillato", "", List.of(), "5 €"),
                                // Rum
                                new MenuItemResponse("bacardi", "Bacardi", "Rum", "", List.of(), "5 €"),
                                new MenuItemResponse("don_papa", "Don Papa", "Rum", "", List.of(), "9 €"),
                                new MenuItemResponse("zacapa", "Zacapa 23 Anni", "Rum", "", List.of(), "12 €"),
                                new MenuItemResponse("j_bally", "J. Bally", "Rum", "", List.of(), ""),
                                new MenuItemResponse("la_hechicera", "La Hechicera", "Rum", "", List.of(), ""),
                                new MenuItemResponse("shot_2cl", "Shot con Distillati Base (2 cl)", "Shot", "", List.of(), "3 €"),
                                new MenuItemResponse("shot_4cl", "Shot con Distillati Base (4 cl)", "Shot", "", List.of(), "5 €")
                        )
                ),
                new MenuSectionResponse(
                        "frullati",
                        "Frullati e Centrifughe",
                        "Frullati vitaminici, salutari e preparati con ingredienti freschi di stagione.",
                        List.of(
                                new MenuItemResponse("frullato_tropicale", "Frullato Tropicale", "Mango, Ananas, Cocco", "Un viaggio esotico cremoso e rinfrescante.", List.of("Fresco", "Vitamina C"), "7 €"),
                                new MenuItemResponse("frutti_di_bosco", "Frutti di Bosco", "Mora, Lampone, Mirtillo", "Il sapore intenso del sottobosco in un mix vellutato.", List.of("Antiossidante"), "7 €")
                        )
                ),
                new MenuSectionResponse(
                        "bevande",
                        "Bevande",
                        "Analcolici, soft drink e alternative leggere.",
                        List.of(
                                new MenuItemResponse("acqua", "Acqua Naturale / Frizzante", "", "", List.of(), "2,50 €"),
                                new MenuItemResponse("coca_cola", "Coca-Cola / Coca-Cola Zero", "", "", List.of(), "3 €"),
                                new MenuItemResponse("fanta", "Fanta", "", "", List.of(), "3 €"),
                                new MenuItemResponse("succhi", "Succhi di Frutta", "", "", List.of(), "3,50 €"),
                                new MenuItemResponse("chinotto", "Chinotto", "", "", List.of(), "3 €"),
                                new MenuItemResponse("schweppes_lemon", "Schweppes Lemon", "", "", List.of(), "3 €"),
                                new MenuItemResponse("te", "Tè Pesca / Limone", "", "", List.of(), "3 €"),
                                new MenuItemResponse("san_bitter", "San Bitter Bianco / Rosso", "", "", List.of(), "3 €"),
                                new MenuItemResponse("cocktail_sp", "Cocktail San Pellegrino", "", "", List.of(), "3 €"),
                                new MenuItemResponse("crodino", "Crodino", "", "", List.of(), "3 €"),
                                new MenuItemResponse("tonica", "Acqua Tonica", "", "", List.of(), "3 €"),
                                new MenuItemResponse("red_bull", "Red Bull", "", "", List.of(), "4 €")
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

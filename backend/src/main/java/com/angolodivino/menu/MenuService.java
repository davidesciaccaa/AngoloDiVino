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
                        "Cocktails",
                        "I grandi classici e le nostre proposte miscelate.",
                        List.of(
                                new MenuItemResponse("Aperol Spritz", "", "(4 cl Aperol, 1 dl Prosecco, Sprite q.b., arancia)", List.of(), ""),
                                new MenuItemResponse("Campari Spritz", "", "(4 cl Campari, 1 dl Prosecco, acqua tonica q.b.)", List.of(), ""),
                                new MenuItemResponse("Campari & Prosecco", "", "(4 cl Campari, 1 dl Prosecco, arancia)", List.of(), ""),
                                new MenuItemResponse("Caipirinha", "", "(4 cl Cachaça, zucchero di canna, lime, ghiaccio tritato)", List.of(), ""),
                                new MenuItemResponse("Caipiroska alla Fragola", "", "", List.of(), ""),
                                new MenuItemResponse("Gin Tonic", "", "(4 cl Gin, acqua tonica)", List.of(), ""),
                                new MenuItemResponse("Hugo", "", "(2 cl succo di lime, 2 cl sciroppo di sambuco, 1 dl Prosecco, Sprite q.b., menta)", List.of(), ""),
                                new MenuItemResponse("Moscow Mule", "", "(1,5 cl succo di lime, 4 cl Vodka, Ginger Beer)", List.of(), ""),
                                new MenuItemResponse("London Mule", "", "(1,5 cl succo di lime, 4 cl Gin, Ginger Beer)", List.of(), ""),
                                new MenuItemResponse("Long Island Iced Tea", "", "(3 cl succo di lime, 2 cl zucchero di canna, 1,5 cl Triple Sec, 1,5 cl Gin, 1,5 cl Vodka, 1,5 cl Rum, 1,5 cl Tequila, Cola)", List.of(), ""),
                                new MenuItemResponse("Japan Iced Tea", "", "(1,5 cl Vodka, 1,5 cl Rum, 1,5 cl Gin, 1,5 cl Midori, 6 cl Sweet & Sour al limone)", List.of(), ""),
                                new MenuItemResponse("Mojito Scuro", "", "(zucchero di canna, lime, menta, rum scuro, acqua frizzante)", List.of(), ""),
                                new MenuItemResponse("Negroni", "", "(3 cl Gin, 3 cl Campari, 3 cl Vermouth rosso, arancia)", List.of(), ""),
                                new MenuItemResponse("Negroni Sbagliato", "", "(3 cl Prosecco, 3 cl Bitter, 3 cl Vermouth rosso)", List.of(), ""),
                                new MenuItemResponse("Sex on the Beach", "", "(4 cl Vodka, 2 cl liquore alla pesca, 4 cl succo d’arancia, 4 cl succo di mirtillo)", List.of(), ""),
                                new MenuItemResponse("Martini Cocktail", "", "(6 cl Gin, 1 cl Martini Dry, scorza di lime, olive)", List.of(), ""),
                                new MenuItemResponse("Espresso Martini", "", "(5 cl Vodka, 2 cl caffè espresso, liquore al caffè, zucchero)", List.of(), ""),
                                new MenuItemResponse("Cosmopolitan", "", "(4 cl Vodka, 1,5 cl Triple Sec, 3 cl succo di mirtillo, 1,5 cl succo di lime, scorza d’arancia, ribes rosso)", List.of(), ""),
                                new MenuItemResponse("Quattro Bianchi", "", "(2 cl Gin, 2 cl Rum, 2 cl Vodka, 2 cl Tequila)", List.of(), "")
                        )
                ),
                new MenuSectionResponse(
                        "vini",
                        "Vini",
                        "Una selezione ricercata di vini bianchi, rosati e rossi, anche biologici.",
                        List.of(
                                new MenuItemResponse("Calice di Vino", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Calice di Prosecco", "", "", List.of(), "4 €"),
                                // Vini Bianchi
                                new MenuItemResponse("Calavento IGP Salento", "Bianco", "", List.of(), "21 €"),
                                new MenuItemResponse("Luna IGP Salento", "Bianco", "", List.of(), "21 €"),
                                new MenuItemResponse("Leverano Vecchia Torre", "Bianco", "", List.of(), "16 €"),
                                new MenuItemResponse("Müller Thurgau", "Bianco", "", List.of(), "21 €"),
                                new MenuItemResponse("Gewürztraminer", "Bianco", "", List.of(), "21 €"),
                                new MenuItemResponse("Trebbiano d’Abruzzo", "Bianco", "", List.of(), "16 €"),
                                new MenuItemResponse("Verdeca Due Trulli", "Bianco", "", List.of(), "18 €"),
                                new MenuItemResponse("Chardonnay", "Bianco", "", List.of(), "18 €"),
                                // Vini Bianchi Bio
                                new MenuItemResponse("Trebbiano d’Abruzzo Bio Vegano", "Bianco Bio", "", List.of(), "19 €"),
                                new MenuItemResponse("Passerina Bio Vegano", "Bianco Bio", "", List.of(), "19 €"),
                                new MenuItemResponse("Pecorino Bio", "Bianco Bio", "", List.of(), "19 €"),
                                new MenuItemResponse("Castel del Monte Bio", "Bianco Bio", "", List.of(), "19 €"),
                                new MenuItemResponse("Vitalba Bio", "Bianco Bio", "", List.of(), "19 €"),
                                new MenuItemResponse("Dharma Bio", "Bianco Bio", "", List.of(), "19 €"),
                                // Spumante Bio
                                new MenuItemResponse("Novebolle D.O.C.", "Spumante Bio", "", List.of(), "19 €"),
                                // Vino Rosato Bio
                                new MenuItemResponse("Castel del Monte Bio", "Rosato Bio", "", List.of(), "19 €"),
                                // Vino Rosso Bio
                                new MenuItemResponse("Castel del Monte Bio", "Rosso Bio", "", List.of(), "19 €"),
                                // Vini Rosati
                                new MenuItemResponse("Leverano DOP Vecchia Torre", "Rosato", "", List.of(), "16 €"),
                                new MenuItemResponse("Negroamaro Vecchia Torre", "Rosato", "", List.of(), "18 €"),
                                new MenuItemResponse("Primitivo Rosato 1932", "Rosato", "", List.of(), "19 €"),
                                new MenuItemResponse("Numero Zero Negroamaro Susumaniello", "Rosato", "", List.of(), "21 €"),
                                new MenuItemResponse("Susumaniello Due Trulli", "Rosato", "", List.of(), "21 €"),
                                // Vini Rossi
                                new MenuItemResponse("Primitivo Vecchia Torre", "Rosso", "", List.of(), "16 €"),
                                new MenuItemResponse("Primitivo Due Trulli", "Rosso", "", List.of(), "18 €"),
                                new MenuItemResponse("Primitivo Vignaioli 68 IGP", "Rosso", "", List.of(), "28 €"),
                                new MenuItemResponse("Primitivo di Manduria 1932", "Rosso", "", List.of(), "21 €"),
                                new MenuItemResponse("Negroamaro Vecchia Torre", "Rosso", "", List.of(), "16 €"),
                                new MenuItemResponse("Negroamaro Due Trulli", "Rosso", "", List.of(), "18 €"),
                                new MenuItemResponse("Negroamaro Manorossa", "Rosso", "", List.of(), "60 €"),
                                new MenuItemResponse("Negroamaro Susumaniello", "Rosso", "", List.of(), "34 €"),
                                new MenuItemResponse("Susumaniello Vigna 14 IGP", "Rosso", "", List.of(), "18 €"),
                                new MenuItemResponse("Nerotavola Sicilia DOC", "Rosso", "", List.of(), "28 €"),
                                new MenuItemResponse("Nero di Troia", "Rosso", "", List.of(), "16 €"),
                                new MenuItemResponse("Aglianico", "Rosso", "", List.of(), "18 €"),
                                new MenuItemResponse("Cabernet Veneto", "Rosso", "", List.of(), "21 €"),
                                new MenuItemResponse("Ripasso Negrar", "Rosso", "", List.of(), "26 €"),
                                new MenuItemResponse("Chianti Classico", "Rosso", "", List.of(), "20 €"),
                                new MenuItemResponse("Brunello", "Rosso", "", List.of(), "40 €"),
                                new MenuItemResponse("Amarone", "Rosso", "", List.of(), "40 €")
                        )
                ),
                new MenuSectionResponse(
                        "amari",
                        "Amari",
                        "Selezione di amari e liquori per chiudere in bellezza.",
                        List.of(
                                new MenuItemResponse("Ramazzotti", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Cynar", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Montenegro", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Fernet Branca Menta", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Averna", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Petrus", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Jagermeister", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Jefferson", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Unicum", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Lucano", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Amaro del capo", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Sambuca", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Caffè borghetti", "", "", List.of(), "4 €"),
                                new MenuItemResponse("Vena Caffè", "", "", List.of(), "4 €")
                        )
                ),
                new MenuSectionResponse(
                        "superalcolici",
                        "Distillati e Rum",
                        "Una selezione di pregiati distillati e rum da meditazione.",
                        List.of(
                                // Distillati
                                new MenuItemResponse("Vecchia Romagna", "Distillato", "", List.of(), "5 €"),
                                new MenuItemResponse("Cointreau", "Distillato", "", List.of(), "5 €"),
                                new MenuItemResponse("Jack Daniel’s", "Distillato", "", List.of(), "6 €"),
                                new MenuItemResponse("Jack Daniel’s Honey", "Distillato", "", List.of(), "6 €"),
                                new MenuItemResponse("Cardinal Mendoza", "Distillato", "", List.of(), "12 €"),
                                new MenuItemResponse("Oban", "Distillato", "", List.of(), "12 €"),
                                new MenuItemResponse("Laphroaig", "Distillato", "", List.of(), "12 €"),
                                new MenuItemResponse("Lagavulin", "Distillato", "", List.of(), "12 €"),
                                new MenuItemResponse("Martini Bianco / Rosso / Dry", "Distillato", "", List.of(), "5 €"),
                                // Rum
                                new MenuItemResponse("Bacardi", "Rum", "", List.of(), "5 €"),
                                new MenuItemResponse("Don Papa", "Rum", "", List.of(), "9 €"),
                                new MenuItemResponse("Zacapa 23 Anni", "Rum", "", List.of(), "12 €"),
                                new MenuItemResponse("J. Bally", "Rum", "", List.of(), ""),
                                new MenuItemResponse("La Hechicera", "Rum", "", List.of(), ""),
                                new MenuItemResponse("Shot con Distillati Base (2 cl)", "Shot", "", List.of(), "3 €"),
                                new MenuItemResponse("Shot con Distillati Base (4 cl)", "Shot", "", List.of(), "5 €")
                        )
                ),
                new MenuSectionResponse(
                        "frullati",
                        "Frullati",
                        "Frullati vitaminici, salutari e preparati con ingredienti freschi di stagione.",
                        List.of(
                                new MenuItemResponse("Frullato Tropicale", "Mango, Ananas, Cocco", "Un viaggio esotico cremoso e rinfrescante.", List.of("Fresco", "Vitamina C"), "7 EUR"),
                                new MenuItemResponse("Frutti di Bosco", "Mora, Lampone, Mirtillo", "Il sapore intenso del sottobosco in un mix vellutato.", List.of("Antiossidante"), "7 EUR")
                        )
                ),
                new MenuSectionResponse(
                        "bevande",
                        "Bevande",
                        "Analcolici, soft drink e alternative leggere.",
                        List.of(
                                new MenuItemResponse("Acqua Naturale / Frizzante", "", "", List.of(), "2,50 €"),
                                new MenuItemResponse("Coca-Cola / Coca-Cola Zero", "", "", List.of(), "3 €"),
                                new MenuItemResponse("Fanta", "", "", List.of(), "3 €"),
                                new MenuItemResponse("Succhi di Frutta", "", "", List.of(), "3,50 €"),
                                new MenuItemResponse("Chinotto", "", "", List.of(), "3 €"),
                                new MenuItemResponse("Schweppes Lemon", "", "", List.of(), "3 €"),
                                new MenuItemResponse("Tè Pesca / Limone", "", "", List.of(), "3 €"),
                                new MenuItemResponse("San Bitter Bianco / Rosso", "", "", List.of(), "3 €"),
                                new MenuItemResponse("Cocktail San Pellegrino", "", "", List.of(), "3 €"),
                                new MenuItemResponse("Crodino", "", "", List.of(), "3 €"),
                                new MenuItemResponse("Acqua Tonica", "", "", List.of(), "3 €"),
                                new MenuItemResponse("Red Bull", "", "", List.of(), "4 €")
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

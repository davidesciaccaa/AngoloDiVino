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
                                        "3 €"
                                ),
                                new MenuItemResponse(
                                        "tagliere misto",
                                        "Tagliere misto",
                                        "",
                                        "",
                                        List.of(),
                                        "10 €"
                                ),
                                new MenuItemResponse(
                                        "fritto misto",
                                        "Fritto misto",
                                        "",
                                        "",
                                        List.of(),
                                        "10 €"
                                ),
                                new MenuItemResponse(
                                        "ostriche",
                                        "Ostriche",
                                        "",
                                        "Ostriche fresche",
                                        List.of("Fresco", "Mare"),
                                        "10 €"
                                )
                        )
                ),
                new MenuSectionResponse(
                        "drink",
                        "Cocktails",
                        "I grandi classici e le nostre proposte miscelate.",
                        List.of(
                                // Pre-Dinner (7 euro) - Aperol/Campari Spritz (6 euro)
                                new MenuItemResponse("americano", "Americano", "Pre-Dinner", "(Vermouth Rosso, Campari, Soda)", List.of(), "7 €"),
                                new MenuItemResponse("bellini", "Bellini", "Pre-Dinner", "(Prosecco, Succo di pesca)", List.of(), "7 €"),
                                new MenuItemResponse("garibaldi", "Garibaldi", "Pre-Dinner", "(Campari, Succo d'Arancia)", List.of(), "7 €"),
                                new MenuItemResponse("negroni", "Negroni", "Pre-Dinner", "(gin, Vermouth rosso, Campari, Fetta d'arancia)", List.of(), "7 €"),
                                new MenuItemResponse("negroni_sbagliato", "Negroni Sbagliato", "Pre-Dinner", "(Vermouth rosso, Campari, prosecco, Fetta d'arancia)", List.of(), "7 €"),
                                new MenuItemResponse("martini_cocktail", "Martini Cocktail", "Pre-Dinner", "(Gin, Vermouth Dry, scorza di lime, olive)", List.of(), "7 €"),
                                new MenuItemResponse("mimosa", "Mimosa", "Pre-Dinner", "(Prosecco, Succo d'arancia)", List.of(), "7 €"),
                                new MenuItemResponse("kir", "Kir", "Pre-Dinner", "(vino bianco fermo, Crème de Cassis)", List.of(), "7 €"),
                                new MenuItemResponse("kir_royale", "Kir Royale", "Pre-Dinner", "(prosecco, Crème de Cassis)", List.of(), "7 €"),
                                new MenuItemResponse("rossini", "Rossini", "Pre-Dinner", "(Prosecco, Fragola)", List.of(), "7 €"),
                                new MenuItemResponse("aperol_spritz", "Aperol Spritz", "Pre-Dinner", "(Prosecco, Aperol, Soda, fetta d'arancia)", List.of(), "6 €"),
                                new MenuItemResponse("campari_spritz", "Campari Spritz", "Pre-Dinner", "(Prosecco, Campari, Soda, fetta d'arancia)", List.of(), "6 €"),
                                new MenuItemResponse("manhattan", "Manhattan", "Pre-Dinner", "(rye whiskey, vermouth rosso, angostura, ciliegina)", List.of(), "7 €"),
                                new MenuItemResponse("dry_manhattan", "Dry Manhattan", "Pre-Dinner", "(rye whiskey, vermouth dry, angostura, buccia di limone)", List.of(), "7 €"),
                                new MenuItemResponse("perfect_manhattan", "Perfect Manhattan", "Pre-Dinner", "(rye whiskey, vermouth rosso, vermouth dry, ciliegina)", List.of(), "7 €"),
                                new MenuItemResponse("rob_roy", "Rob Roy", "Pre-Dinner", "(Scotch whisky, vermouth rosso, angostura, ciliegina)", List.of(), "7 €"),
                                new MenuItemResponse("dry_martini", "Dry Martini", "Pre-Dinner / Martini Cocktails", "(gin, vermouth dry, sprizzo di buccia di limone)", List.of(), "7 €"),
                                new MenuItemResponse("vodka_martini", "Vodka Martini", "Pre-Dinner / Martini Cocktails", "(vodka, vermouth dry, sprizzo di buccia di limone)", List.of(), "7 €"),
                                new MenuItemResponse("sweet_martini", "Sweet Martini", "Pre-Dinner / Martini Cocktails", "(gin, vermouth rosso, ciliegina)", List.of(), "7 €"),
                                new MenuItemResponse("perfect_martini", "Perfect Martini", "Pre-Dinner / Martini Cocktails", "(gin, vermouth rosso, vermouth dry, ciliegina)", List.of(), "7 €"),

                                // After Dinner (8 euro)
                                new MenuItemResponse("black_russian", "Black Russian", "After Dinner", "(Vodka, Kahlua)", List.of(), "8 €"),
                                new MenuItemResponse("white_russian", "White Russian", "After Dinner", "(Vodka, Kahlua, Crema di latte)", List.of(), "8 €"),
                                new MenuItemResponse("cosmopolitan", "Cosmopolitan", "After Dinner", "(Vodka, Triple Sec, Cranberry, Limone)", List.of(), "8 €"),
                                new MenuItemResponse("daiquiri", "Daiquiri", "After Dinner", "(Rum, Succo di limone o lime, Sciroppo di zucchero)", List.of(), "8 €"),
                                new MenuItemResponse("french_connection", "French connection", "After Dinner", "(Cognac, Disaronno)", List.of(), "8 €"),
                                new MenuItemResponse("mexican_passion", "Mexican passion", "After Dinner", "(tequila blanco, triple sec, succo di lime, succo di passion fruit, fetta di limone)", List.of(), "8 €"),
                                new MenuItemResponse("gin_fizz", "Gin Fizz", "After Dinner", "(Gin, Succo di limone, Sciroppo di zucchero, Soda water)", List.of(), "8 €"),
                                new MenuItemResponse("godfather", "Godfather", "After Dinner", "(Whisky, Disaronno)", List.of(), "8 €"),
                                new MenuItemResponse("godmother", "Godmother", "After Dinner", "(Vodka, Disaronno)", List.of(), "8 €"),
                                new MenuItemResponse("kamikaze", "Kamikaze", "After Dinner", "(vodka, triple sec, succo di limone)", List.of(), "8 €"),
                                new MenuItemResponse("whitelady", "Whitelady", "After Dinner", "(Gin, Triple Sec, Succo di limone)", List.of(), "8 €"),
                                new MenuItemResponse("limbo", "Limbo", "After Dinner", "(Rum bianco, Creme de banana, Succo d'arancia)", List.of(), "8 €"),
                                new MenuItemResponse("margarita", "Margarita", "After Dinner", "(Tequila, Triple Sec, Succo di limone o lime)", List.of(), "8 €"),
                                new MenuItemResponse("orgasm", "Orgasm", "After Dinner", "(Baileys, Amaretto, Kahlua)", List.of(), "8 €"),
                                new MenuItemResponse("melon_ball", "Melon ball", "After Dinner", "(Midori, Vodka, Succo d'arancia)", List.of(), "8 €"),
                                new MenuItemResponse("paradise", "Paradise", "After Dinner", "(Gin, Apricot brandy, Succo d'arancia)", List.of(), "8 €"),
                                new MenuItemResponse("rusty_nail", "Rusty nail", "After Dinner", "(Scotch whisky, Drambuie)", List.of(), "8 €"),
                                new MenuItemResponse("sidecar", "Sidecar", "After Dinner", "(Cognac, Triple sec o Cointreau, Succo di limone)", List.of(), "8 €"),
                                new MenuItemResponse("stinger", "Stinger", "After Dinner", "(Brandy, Crema di menta bianca)", List.of(), "8 €"),

                                // Long Drinks (8 euro)
                                new MenuItemResponse("cuba_libre", "Cuba Libre", "Long Drinks", "(Rum, Coca cola, Succo di lime)", List.of(), "8 €"),
                                new MenuItemResponse("lynchburg_lemonade", "Lynchburg lemonade", "Long Drinks", "(jack Daniel's, triple sec, succo di limone, sprite)", List.of(), "8 €"),
                                new MenuItemResponse("pina_colada", "Pina colada", "Long Drinks", "(Rum, Crema di cocco, Ananas)", List.of(), "8 €"),
                                new MenuItemResponse("pina_colada_varianti", "Varianti di Pina Colada", "Long Drinks", "", List.of(), "8 €"),
                                new MenuItemResponse("screwdriver", "Screwdriver", "Long Drinks", "(Vodka, Succo d'arancia)", List.of(), "8 €"),
                                new MenuItemResponse("sex_on_the_beach", "Sex on the beach", "Long Drinks", "(vodka, liquore di pesca, succo d'arancia, succo di cranberry)", List.of(), "8 €"),
                                new MenuItemResponse("tequila_sunrise", "Tequila sunrise", "Long Drinks", "(Tequila, Succo d'arancia, Granatina)", List.of(), "8 €"),

                                // Iced Tea (6/7 euro)
                                new MenuItemResponse("long_island_iced_tea", "Long Island Ice tea", "Iced Tea", "(Vodka, Gin, Rum, Triple Sec, Sweet & Sour, Top of Coke)", List.of(), "8 €"),
                                new MenuItemResponse("texas_iced_tea", "Texas Ice tea", "Iced Tea", "(Vodka, Gin, Rum, Triple Sec, Tequila, Sweet & Sour, Top of Coke)", List.of(), "8 €"),
                                new MenuItemResponse("japanese_iced_tea", "Japanese Ice tea", "Iced Tea", "(Vodka, Gin, Rum, Midori, Sweet & Sour, Lemonsoda)", List.of(), "8 €"),
                                new MenuItemResponse("italian_iced_tea", "Italian ice tea", "Iced Tea", "(Vodka, Gin, Rum, Amaretto, Sweet & Sour, Lemonsoda)", List.of(), "8 €"),
                                new MenuItemResponse("california_iced_tea", "California ice tea", "Iced Tea", "(Vodka, Gin, Rum, Triple Sec, Sweet & Sour, Succo d'arancia)", List.of(), "8 €"),

                                // Pestati e Frozen (8 euro)
                                new MenuItemResponse("caipirinha", "Caipirinha", "Pestati e Frozen", "(Cachaca, Lime, Zucchero di canna)", List.of(), "8 €"),
                                new MenuItemResponse("caipiroska", "Caipiroska", "Pestati e Frozen", "(Vodka, Lime, Zucchero di canna)", List.of(), "8 €"),
                                new MenuItemResponse("strawberry_caipiroska", "Strawberry caipiroska", "Pestati e Frozen", "(Vodka, Lime, Zucchero di canna, Fragola)", List.of(), "8 €"),
                                new MenuItemResponse("caipirissima", "Caipirissima", "Pestati e Frozen", "(Rum, Lime, Zucchero di canna)", List.of(), "8 €"),
                                new MenuItemResponse("caipiriteqa", "Caipiriteqa", "Pestati e Frozen", "(Tequila, Lime, Zucchero di canna)", List.of(), "8 €"),
                                new MenuItemResponse("mojito", "Mojito", "Pestati e Frozen", "(Rum, Lime, Foglie di menta, Watersoda)", List.of(), "8 €"),
                                new MenuItemResponse("frozen_slammer", "Frozen slammer", "Pestati e Frozen", "(Southern comfort, Disaronno, Fragola, Sciroppo e frutta)", List.of(), "8 €"),
                                new MenuItemResponse("frozen_daiquiri", "Frozen daiquiri", "Pestati e Frozen", "(Rum, Fragola, Banana, Ananas, ecc, Sweet & Sour)", List.of(), "8 €"),
                                new MenuItemResponse("frozen_margarita", "Frozen margarita", "Pestati e Frozen", "(Tequila, Fragola, Banana, Ananas, ecc, Sweet & Sour)", List.of(), "8 €")
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
                                new MenuItemResponse("tacco_barocco_puglia_igp", "Tacco Barocco - Puglia IGP", "", "Corposo ed equilibrato, perfetto per accompagnare taglieri. Cantina Sampietrana. 750ml", List.of(), "6 € - 24 €"),
                                // Rosati
                                new MenuItemResponse("rosato_salento", "Rosato del Salento", "Rosati", "Fresco, fruttato, con note di ciliegia e lampone. 750ml", List.of(), "5 € / 18 €"),
                                // Bollicine
                                new MenuItemResponse("prosecco_doc", "Prosecco D.O.C.", "Bollicine", "Perlage fine e persistente, ideale come aperitivo. 750ml", List.of(), "4 € / 18 €")
                        )
                ),
                new MenuSectionResponse(
                        "amari",
                        "Amari e Liquori",
                        "Selezione di amari e liquori per chiudere in bellezza.",
                        List.of(
                                new MenuItemResponse("montenegro", "Montenegro", "", "", List.of(), "4 €"),
                                new MenuItemResponse("del_capo", "Amaro del Capo", "", "", List.of(), "4 €"),
                                new MenuItemResponse("jagermeister", "Jagermeister", "", "", List.of(), "4 €"),
                                new MenuItemResponse("unicum", "Unicum", "", "", List.of(), "4 €"),
                                new MenuItemResponse("sambuca", "Sambuca", "", "", List.of(), "4 €"),
                                new MenuItemResponse("kahlua", "Kahlua", "", "", List.of(), "4 €"),
                                new MenuItemResponse("limoncello", "Limoncello", "", "", List.of(), "4 €"),
                                new MenuItemResponse("cointreau", "Cointreau", "", "", List.of(), "4 €"),
                                new MenuItemResponse("grand_marnier", "Grand Marnier", "", "", List.of(), "4 €"),
                                new MenuItemResponse("pernod", "Pernod", "", "", List.of(), "4 €"),
                                new MenuItemResponse("batida_de_coco", "Batida de coco", "", "", List.of(), "4 €"),
                                new MenuItemResponse("midori", "Midori", "", "", List.of(), "4 €"),
                                new MenuItemResponse("malibu", "Malibù", "", "", List.of(), "4 €")
                        )
                ),
                new MenuSectionResponse(
                        "superalcolici",
                        "Superalcolici",
                        "Una selezione di pregiati distillati e rum da meditazione.",
                        List.of(
                                // Whisky
                                new MenuItemResponse("jack_daniels", "Jack Daniel's", "Whisky", "4cl", List.of(), "6 €"),
                                new MenuItemResponse("johnnie_walker", "Johnnie Walker", "Whisky", "4cl", List.of(), "6 €"),
                                new MenuItemResponse("lagavulin", "Lagavulin", "Whisky", "4cl", List.of(), "9 €"),
                                // Vodka
                                new MenuItemResponse("absolut", "Absolut", "Vodka", "", List.of(), "6 €"),
                                new MenuItemResponse("moskovskaya", "Moskovskaya", "Vodka", "", List.of(), "5 €"),
                                new MenuItemResponse("stolichnaya", "Stolichnaya", "Vodka", "", List.of(), "6 €"),
                                new MenuItemResponse("ciroc", "Cîroc", "Vodka", "", List.of(), "6 €"),
                                // Rum
                                new MenuItemResponse("aniversario", "Pampero Aniversario", "Rum", "", List.of(), "6 €"),
                                new MenuItemResponse("havana_3", "Havana Club 3", "Rum", "", List.of(), "6 €"),
                                new MenuItemResponse("havana_7", "Havana Club 7", "Rum", "", List.of(), "6 €"),
                                new MenuItemResponse("zacapa", "Zacapa", "Rum", "", List.of(), "6 €"),
                                new MenuItemResponse("kraken", "Kraken", "Rum", "", List.of(), "6 €"),
                                // Gin
                                new MenuItemResponse("tanqueray", "Tanqueray", "Gin", "", List.of(), "6 €"),
                                new MenuItemResponse("bombay", "Bombay Sapphire", "Gin", "", List.of(), "6 €"),
                                new MenuItemResponse("gin_mare", "Gin Mare", "Gin", "", List.of(), "9 €"),
                                new MenuItemResponse("hendricks", "Hendrick's", "Gin", "", List.of(), "9 €"),
                                new MenuItemResponse("malfy_originale", "Malfy Originale", "Gin", "", List.of(), "7 €"),
                                new MenuItemResponse("malfy_pompelmo", "Malfy Pompelmo", "Gin", "", List.of(), "7 €"),
                                new MenuItemResponse("malfy_arancia", "Malfy Arancia", "Gin", "", List.of(), "7 €"),
                                new MenuItemResponse("cubical", "Cubical", "Gin", "", List.of(), "8 €"),
                                new MenuItemResponse("citadelle", "Gin Citadelle", "Gin", "", List.of(), "7 €"),
                                new MenuItemResponse("roku", "Roku", "Gin", "", List.of(), "7 €"),
                                new MenuItemResponse("nordes", "Nordés", "Gin", "", List.of(), "7 €"),
                                // Tequila & Mezcal
                                new MenuItemResponse("jose_cuervo", "Jose Cuervo", "Tequila", "", List.of(), "5 €"),
                                new MenuItemResponse("fortaleza", "Fortaleza", "Tequila", "", List.of(), "9 €"),
                                // Grappe & Cognac
                                new MenuItemResponse("grappa_bianca", "Grappa Bianca", "Grappe & Cognac", "", List.of(), "6 €"),
                                new MenuItemResponse("grappa_barricata", "Grappa Barricata Amarone della Valpolicella", "Grappe & Cognac", "", List.of(), "9 €"),
                                new MenuItemResponse("vecchia_romagna", "Vecchia Romagna", "Grappe & Cognac", "", List.of(), "5 €"),
                                new MenuItemResponse("martell", "Martell", "Grappe & Cognac", "", List.of(), "8 €"),
                                new MenuItemResponse("armagnac", "Armagnac", "Grappe & Cognac", "In arrivo", List.of(), "-"),
                                // Shots
                                new MenuItemResponse("shot_2cl", "Shot con Distillati Base (2 cl)", "Shot", "", List.of(), "3 €"),
                                new MenuItemResponse("shot_4cl", "Shot con Distillati Base (4 cl)", "Shot", "", List.of(), "5 €")
                        )
                ),
                new MenuSectionResponse(
                        "bevande",
                        "Soft Drinks",
                        "Analcolici, mocktail e alternative leggere.",
                        List.of(
                                // Mocktails (Analcolici)
                                new MenuItemResponse("virgin_colada", "Virgin colada", "Mocktail", "(Latte di cocco o Pina colada mix, Succo d'arancia)", List.of(), "5 €"),
                                new MenuItemResponse("daiquiri_strawberry", "Daiquiri Strawberry", "Mocktail", "(Sciroppo di fragola, Succo di lime o limone)", List.of(), "5 €"),
                                new MenuItemResponse("florida", "Florida", "Mocktail", "(Succo di pompelmo, Succo d'arancia, Succo di lime o limone, Sciroppo di zucchero, Soda water)", List.of(), "5 €"),
                                new MenuItemResponse("shirley_temple", "Shirley Temple", "Mocktail", "(Ginger ale, Sciroppo di granatina)", List.of(), "5 €"),
                                new MenuItemResponse("red_peach", "Red peach", "Mocktail", "(Sciroppo di fragola, Succo di pesca, Succo d'ananas)", List.of(), "5 €"),
                                new MenuItemResponse("sweet_strawberry", "Sweet strawberry", "Mocktail", "(Sciroppo di fragola, Succo d'arancia, Top of Sprite)", List.of(), "5 €"),

                                // Bevande Classiche
                                new MenuItemResponse("centrifuga", "Centrifuga", "", "In base alla stagione", List.of(), "5 €"),
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

package com.angolodivino.menu;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CocktailService {

    public List<CocktailResponse> findSignatureCocktails() {
        return List.of(
                new CocktailResponse(
                        "Rubino Sour",
                        "Vino rosso, agrumi",
                        "Un sour vellutato con vino rosso ridotto, limone fresco e albume.",
                        List.of("Vino rosso", "Limone", "Sciroppo speziato"),
                        "12 EUR"
                ),
                new CocktailResponse(
                        "Spritz del Vicolo",
                        "Bitter, bollicine",
                        "Aperitivo verticale, asciutto, con erbe amare e prosecco extra dry.",
                        List.of("Bitter italiano", "Prosecco", "Soda"),
                        "10 EUR"
                ),
                new CocktailResponse(
                        "Notturno Bianco",
                        "Gin, uva bianca",
                        "Gin floreale, mosto d'uva bianca e una chiusura fresca di salvia.",
                        List.of("Gin", "Mosto d'uva", "Salvia"),
                        "13 EUR"
                )
        );
    }
}

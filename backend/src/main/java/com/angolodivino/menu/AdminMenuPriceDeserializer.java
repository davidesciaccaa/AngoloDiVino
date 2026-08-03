package com.angolodivino.menu;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

/** Strict parser for new admin/API input; legacy scalar prices are deliberately excluded. */
public final class AdminMenuPriceDeserializer extends JsonDeserializer<MenuPrice> {

    @Override
    public MenuPrice deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        try {
            JsonToken token = parser.currentToken();
            if (token == JsonToken.VALUE_NULL) {
                return null;
            }
            if (token == JsonToken.VALUE_STRING) {
                if (parser.getValueAsString().isBlank()) {
                    return null;
                }
                throw new IllegalArgumentException(
                        "Il prezzo API deve essere null, vuoto o un oggetto strutturato");
            }
            if (token != JsonToken.START_OBJECT) {
                throw new IllegalArgumentException("Il prezzo API deve essere un oggetto strutturato");
            }

            JsonNode node = parser.getCodec().readTree(parser);
            return MenuPriceDeserializer.fromStructuredObject(node);
        } catch (IllegalArgumentException exception) {
            throw JsonMappingException.from(
                    parser,
                    exception.getMessage() == null ? "Prezzo API non valido" : exception.getMessage());
        }
    }
}

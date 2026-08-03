package com.angolodivino.menu;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** Reads the structured price model and every supported legacy representation. */
public final class MenuPriceDeserializer extends JsonDeserializer<MenuPrice> {

    private static final String MOJIBAKE_EURO = "\u00e2\u201a\u00ac";
    private static final Pattern LEGACY_AMOUNT = Pattern.compile("^\\d+(?:[.,]\\d{1,2})?\\s*€?$");
    private static final Pattern HYPHEN_SEPARATOR = Pattern.compile("\\s+-\\s+");

    @Override
    public MenuPrice deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        try {
            JsonToken token = parser.currentToken();
            if (token == JsonToken.VALUE_NULL) {
                return null;
            }
            if (token == JsonToken.VALUE_STRING) {
                return parseLegacy(parser.getValueAsString());
            }
            if (token != null && token.isNumeric()) {
                return MenuPrice.single(parser.getDecimalValue());
            }

            JsonNode node = parser.getCodec().readTree(parser);
            if (node.isArray()) {
                return fromNumericArray(node);
            }
            if (node.isObject()) {
                return fromStructuredObject(node);
            }
            throw invalid(parser, "Formato prezzo non supportato");
        } catch (IllegalArgumentException exception) {
            throw invalid(parser, exception.getMessage());
        }
    }

    public static MenuPrice parseLegacy(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Prezzo legacy nullo");
        }
        String value = raw.strip().replace(MOJIBAKE_EURO, "€");
        if ("-".equals(value)) {
            return null;
        }

        String[] parts;
        if (value.contains("/")) {
            parts = value.split("/", -1);
        } else if (HYPHEN_SEPARATOR.matcher(value).find()) {
            parts = HYPHEN_SEPARATOR.split(value, -1);
        } else {
            parts = new String[] {value};
        }

        List<PriceOption> options = new ArrayList<>(parts.length);
        for (String part : parts) {
            String amount = part.strip();
            if (!LEGACY_AMOUNT.matcher(amount).matches()) {
                throw new IllegalArgumentException("Prezzo legacy ambiguo o malformato: " + raw);
            }
            String numeric = amount.replace("€", "").strip().replace(',', '.');
            options.add(new PriceOption(null, new BigDecimal(numeric)));
        }
        return new MenuPrice(options);
    }

    private static MenuPrice fromNumericArray(JsonNode node) {
        List<PriceOption> options = new ArrayList<>();
        for (JsonNode amount : node) {
            if (!amount.isNumber()) {
                throw new IllegalArgumentException("Gli importi del prezzo devono essere numerici");
            }
            options.add(new PriceOption(null, amount.decimalValue()));
        }
        return new MenuPrice(options);
    }

    private static MenuPrice fromStructuredObject(JsonNode node) {
        for (Map.Entry<String, JsonNode> field : node.properties()) {
            String name = field.getKey();
            if (!"options".equals(name)) {
                throw new IllegalArgumentException("Campo prezzo non supportato: " + name);
            }
        }

        JsonNode optionsNode = node.get("options");
        if (optionsNode == null || !optionsNode.isArray()) {
            throw new IllegalArgumentException("Il prezzo strutturato richiede un array options");
        }
        List<PriceOption> options = new ArrayList<>();
        for (JsonNode optionNode : optionsNode) {
            if (!optionNode.isObject()) {
                throw new IllegalArgumentException("Opzione prezzo non valida");
            }
            for (Map.Entry<String, JsonNode> field : optionNode.properties()) {
                String name = field.getKey();
                if (!"label".equals(name) && !"amount".equals(name)) {
                    throw new IllegalArgumentException("Campo opzione prezzo non supportato: " + name);
                }
            }
            JsonNode amountNode = optionNode.get("amount");
            if (amountNode == null || !amountNode.isNumber()) {
                throw new IllegalArgumentException("L'importo dell'opzione deve essere numerico");
            }
            PriceOptionLabel label = null;
            JsonNode labelNode = optionNode.get("label");
            if (labelNode != null && !labelNode.isNull()) {
                if (!labelNode.isTextual()) {
                    throw new IllegalArgumentException("Etichetta prezzo non valida");
                }
                label = PriceOptionLabel.fromJson(labelNode.textValue());
            }
            options.add(new PriceOption(label, amountNode.decimalValue()));
        }
        return new MenuPrice(options);
    }

    private static JsonMappingException invalid(JsonParser parser, String message) {
        return JsonMappingException.from(parser, message == null ? "Prezzo non valido" : message);
    }
}

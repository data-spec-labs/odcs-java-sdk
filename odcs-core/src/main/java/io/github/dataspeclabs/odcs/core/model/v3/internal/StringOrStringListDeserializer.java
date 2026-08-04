package io.github.dataspeclabs.odcs.core.model.v3.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Normalizes a JSON string or string array into {@code List<String>}
 * (used for relationship {@code from}/{@code to} fields).
 */
public final class StringOrStringListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();

        if (token == JsonToken.VALUE_STRING) {
            return List.of(parser.getText());
        }

        if (token == JsonToken.START_ARRAY) {
            List<String> values = new ArrayList<>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                if (parser.currentToken() == JsonToken.VALUE_STRING) {
                    values.add(parser.getText());
                } else if (parser.currentToken() == JsonToken.VALUE_NULL) {
                    values.add(null);
                } else {
                    values.add(parser.getValueAsString());
                }
            }
            return Collections.unmodifiableList(values);
        }

        if (token == JsonToken.VALUE_NULL) {
            return null;
        }

        return List.of(parser.getValueAsString());
    }
}

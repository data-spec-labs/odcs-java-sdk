package io.github.dataspeclabs.odcs.core.model.v3.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import io.github.dataspeclabs.odcs.core.model.v3.Team;
import io.github.dataspeclabs.odcs.core.model.v3.TeamMember;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Deserializes {@link Team} from either the v3.1+ object-with-members shape
 * or the deprecated flat array of {@link TeamMember}.
 */
public final class TeamDeserializer extends JsonDeserializer<Team> {

    @Override
    public Team deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();

        if (token == JsonToken.START_ARRAY) {
            CollectionType listType = mapper.getTypeFactory()
                    .constructCollectionType(List.class, TeamMember.class);
            List<TeamMember> members = mapper.readValue(parser, listType);
            return new Team(null, null, null, members);
        }

        if (token == JsonToken.START_OBJECT) {
            String id = null;
            String name = null;
            String description = null;
            List<TeamMember> members = null;

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String field = parser.currentName();
                parser.nextToken();
                switch (field) {
                    case "id" -> id = parser.getValueAsString();
                    case "name" -> name = parser.getValueAsString();
                    case "description" -> description = parser.getValueAsString();
                    case "members" -> {
                        CollectionType listType = mapper.getTypeFactory()
                                .constructCollectionType(List.class, TeamMember.class);
                        members = mapper.readValue(parser, listType);
                    }
                    default -> parser.skipChildren();
                }
            }
            return new Team(id, name, description, members);
        }

        if (token == JsonToken.VALUE_NULL) {
            return null;
        }

        return new Team(null, null, null, Collections.emptyList());
    }
}

package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.dataspeclabs.odcs.core.model.v3.internal.StringOrStringListDeserializer;

import java.util.List;

/**
 * Foreign-key (or future) relationship between schema elements.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Relationship(
        @JsonProperty("type") RelationshipType type,
        @JsonProperty("from")
        @JsonDeserialize(using = StringOrStringListDeserializer.class)
        List<String> from,
        @JsonProperty("to")
        @JsonDeserialize(using = StringOrStringListDeserializer.class)
        List<String> to,
        @JsonProperty("description") String description,
        @JsonProperty("customProperties") List<CustomProperty> customProperties
) {
    @JsonCreator
    public Relationship {
        if (type == null) {
            type = RelationshipType.FOREIGN_KEY;
        }
    }
}

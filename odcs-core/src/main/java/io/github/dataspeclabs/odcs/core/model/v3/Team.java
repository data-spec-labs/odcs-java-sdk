package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.dataspeclabs.odcs.core.model.v3.internal.TeamDeserializer;

import java.util.List;

/**
 * Team owning the data contract.
 * Deserialized via {@link TeamDeserializer} to also accept the deprecated flat-array shape.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = TeamDeserializer.class)
public record Team(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("members") List<TeamMember> members
) {
    @JsonCreator
    public Team {
    }
}

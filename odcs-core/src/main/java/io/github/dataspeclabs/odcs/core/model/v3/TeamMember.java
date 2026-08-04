package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * Member of a data contract team.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TeamMember(
        @JsonProperty("username") String username,
        @JsonProperty("role") String role,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("dateIn") LocalDate dateIn,
        @JsonProperty("dateOut") LocalDate dateOut,
        @JsonProperty("replacedByUsername") String replacedByUsername,
        @JsonProperty("customProperties") List<CustomProperty> customProperties
) {
    @JsonCreator
    public TeamMember {
    }
}

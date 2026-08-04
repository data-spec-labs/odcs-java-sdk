package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * High-level description of a data contract or description block.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Description(
        @JsonProperty("purpose") String purpose,
        @JsonProperty("limitations") String limitations,
        @JsonProperty("usage") String usage,
        @JsonProperty("authoritativeDefinitions") List<AuthoritativeDefinition> authoritativeDefinitions,
        @JsonProperty("customProperties") List<CustomProperty> customProperties
) {
    @JsonCreator
    public Description {
    }
}

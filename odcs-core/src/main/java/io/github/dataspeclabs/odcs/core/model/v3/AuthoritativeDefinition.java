package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Link to an authoritative external definition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthoritativeDefinition(
        @JsonProperty("url") String url,
        @JsonProperty("type") String type,
        @JsonProperty("description") String description
) {
    @JsonCreator
    public AuthoritativeDefinition {
    }
}

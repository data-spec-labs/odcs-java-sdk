package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Custom extension property (key/value pair).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomProperty(
        @JsonProperty("id") String id,
        @JsonProperty("property") String property,
        @JsonProperty("value") Object value,
        @JsonProperty("description") String description
) {
    @JsonCreator
    public CustomProperty {
    }
}

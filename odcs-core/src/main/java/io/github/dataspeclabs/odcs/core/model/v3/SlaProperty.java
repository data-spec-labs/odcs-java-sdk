package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Service-level agreement property.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SlaProperty(
        @JsonProperty("property") String property,
        @JsonProperty("value") Object value,
        @JsonProperty("valueExt") Object valueExt,
        @JsonProperty("unit") String unit,
        @JsonProperty("element") String element,
        @JsonProperty("driver") String driver,
        @JsonProperty("scheduler") String scheduler,
        @JsonProperty("schedule") String schedule,
        @JsonProperty("customProperties") List<CustomProperty> customProperties
) {
    @JsonCreator
    public SlaProperty {
    }
}

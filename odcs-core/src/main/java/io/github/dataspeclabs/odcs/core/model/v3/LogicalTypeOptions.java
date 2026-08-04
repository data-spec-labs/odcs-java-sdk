package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * Additional optional metadata describing a logical type.
 * Validity of fields for a given {@link LogicalType} is a validator concern.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LogicalTypeOptions(
        @JsonProperty("format") String format,
        @JsonProperty("minLength") Integer minLength,
        @JsonProperty("maxLength") Integer maxLength,
        @JsonProperty("pattern") String pattern,
        @JsonProperty("minimum") BigDecimal minimum,
        @JsonProperty("maximum") BigDecimal maximum,
        @JsonProperty("exclusiveMinimum") BigDecimal exclusiveMinimum,
        @JsonProperty("exclusiveMaximum") BigDecimal exclusiveMaximum,
        @JsonProperty("multipleOf") BigDecimal multipleOf,
        @JsonProperty("timezone") String timezone,
        @JsonProperty("defaultTimezone") String defaultTimezone,
        @JsonProperty("minItems") Integer minItems,
        @JsonProperty("maxItems") Integer maxItems,
        @JsonProperty("uniqueItems") Boolean uniqueItems,
        @JsonProperty("minProperties") Integer minProperties,
        @JsonProperty("maxProperties") Integer maxProperties,
        @JsonProperty("required") List<String> required
) {
    @JsonCreator
    public LogicalTypeOptions {
    }
}

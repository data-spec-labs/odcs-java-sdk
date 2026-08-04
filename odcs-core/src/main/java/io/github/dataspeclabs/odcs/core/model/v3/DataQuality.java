package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Data quality rule attached to a schema element.
 * Semantic constraints (exactly one operator, per-type required fields) are
 * enforced by ODCSSpecValidator, not by this model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DataQuality(
        @JsonProperty("type") DataQualityType type,
        @JsonProperty("description") String description,
        @JsonProperty("metric") DataQualityMetric metric,
        @JsonProperty("query") String query,
        @JsonProperty("engine") String engine,
        @JsonProperty("implementation") Object implementation,
        @JsonProperty("mustBe") Object mustBe,
        @JsonProperty("mustNotBe") Object mustNotBe,
        @JsonProperty("mustBeGreaterThan") Object mustBeGreaterThan,
        @JsonProperty("mustBeGreaterOrEqualTo") Object mustBeGreaterOrEqualTo,
        @JsonProperty("mustBeLessThan") Object mustBeLessThan,
        @JsonProperty("mustBeLessOrEqualTo") Object mustBeLessOrEqualTo,
        @JsonProperty("mustBeBetween") List<Object> mustBeBetween,
        @JsonProperty("mustNotBeBetween") List<Object> mustNotBeBetween,
        @JsonProperty("unit") DataQualityUnit unit,
        @JsonProperty("dimension") DataQualityDimension dimension,
        @JsonProperty("severity") String severity,
        @JsonProperty("businessImpact") String businessImpact,
        @JsonProperty("method") String method,
        @JsonProperty("scheduler") String scheduler,
        @JsonProperty("schedule") String schedule,
        @JsonProperty("rule") @Deprecated String rule,
        @JsonProperty("customProperties") List<CustomProperty> customProperties
) {
    @JsonCreator
    public DataQuality {
    }
}

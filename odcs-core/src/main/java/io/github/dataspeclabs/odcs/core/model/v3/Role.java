package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * IAM / access role for the dataset.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Role(
        @JsonProperty("role") String role,
        @JsonProperty("access") String access,
        @JsonProperty("firstLevelApprovers") String firstLevelApprovers,
        @JsonProperty("secondLevelApprovers") String secondLevelApprovers,
        @JsonProperty("description") String description,
        @JsonProperty("customProperties") List<CustomProperty> customProperties
) {
    @JsonCreator
    public Role {
    }
}

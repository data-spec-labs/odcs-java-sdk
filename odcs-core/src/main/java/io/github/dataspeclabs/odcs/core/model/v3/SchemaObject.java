package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Schema object (table / document / topic / file) within a data contract.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SchemaObject(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("physicalName") String physicalName,
        @JsonProperty("physicalType") String physicalType,
        @JsonProperty("logicalType") LogicalType logicalType,
        @JsonProperty("description") String description,
        @JsonProperty("businessName") String businessName,
        @JsonProperty("dataGranularityDescription") String dataGranularityDescription,
        @JsonProperty("properties") List<SchemaProperty> properties,
        @JsonProperty("relationships") List<Relationship> relationships,
        @JsonProperty("authoritativeDefinitions") List<AuthoritativeDefinition> authoritativeDefinitions,
        @JsonProperty("quality") List<DataQuality> quality,
        @JsonProperty("tags") List<String> tags,
        @JsonProperty("customProperties") List<CustomProperty> customProperties
) {
    @JsonCreator
    public SchemaObject {
    }
}

package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Schema property (column / field) within an object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SchemaProperty(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("physicalName") String physicalName,
        @JsonProperty("physicalType") String physicalType,
        @JsonProperty("description") String description,
        @JsonProperty("businessName") String businessName,
        @JsonProperty("logicalType") LogicalType logicalType,
        @JsonProperty("logicalTypeOptions") LogicalTypeOptions logicalTypeOptions,
        @JsonProperty("primaryKey") Boolean primaryKey,
        @JsonProperty("primaryKeyPosition") Integer primaryKeyPosition,
        @JsonProperty("required") Boolean required,
        @JsonProperty("unique") Boolean unique,
        @JsonProperty("partitioned") Boolean partitioned,
        @JsonProperty("partitionKeyPosition") Integer partitionKeyPosition,
        @JsonProperty("classification") String classification,
        @JsonProperty("encryptedName") String encryptedName,
        @JsonProperty("transformSourceObjects") List<String> transformSourceObjects,
        @JsonProperty("transformLogic") String transformLogic,
        @JsonProperty("transformDescription") String transformDescription,
        @JsonProperty("examples") List<Object> examples,
        @JsonProperty("criticalDataElement") Boolean criticalDataElement,
        @JsonProperty("items") SchemaProperty items,
        @JsonProperty("properties") List<SchemaProperty> properties,
        @JsonProperty("relationships") List<Relationship> relationships,
        @JsonProperty("authoritativeDefinitions") List<AuthoritativeDefinition> authoritativeDefinitions,
        @JsonProperty("quality") List<DataQuality> quality,
        @JsonProperty("tags") List<String> tags,
        @JsonProperty("customProperties") List<CustomProperty> customProperties
) {
    @JsonCreator
    public SchemaProperty {
    }
}

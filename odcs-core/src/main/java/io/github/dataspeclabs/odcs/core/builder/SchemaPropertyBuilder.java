package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.AuthoritativeDefinition;
import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;
import io.github.dataspeclabs.odcs.core.model.v3.DataQuality;
import io.github.dataspeclabs.odcs.core.model.v3.LogicalType;
import io.github.dataspeclabs.odcs.core.model.v3.LogicalTypeOptions;
import io.github.dataspeclabs.odcs.core.model.v3.Relationship;
import io.github.dataspeclabs.odcs.core.model.v3.SchemaProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link SchemaProperty}.
 */
public final class SchemaPropertyBuilder {

    private String id;
    private String name;
    private String physicalName;
    private String physicalType;
    private String description;
    private String businessName;
    private LogicalType logicalType;
    private LogicalTypeOptions logicalTypeOptions;
    private Boolean primaryKey;
    private Integer primaryKeyPosition;
    private Boolean required;
    private Boolean unique;
    private Boolean partitioned;
    private Integer partitionKeyPosition;
    private String classification;
    private String encryptedName;
    private List<String> transformSourceObjects;
    private String transformLogic;
    private String transformDescription;
    private List<Object> examples;
    private Boolean criticalDataElement;
    private SchemaProperty items;
    private List<SchemaProperty> properties;
    private List<Relationship> relationships;
    private List<AuthoritativeDefinition> authoritativeDefinitions;
    private List<DataQuality> quality;
    private List<String> tags;
    private List<CustomProperty> customProperties;

    public SchemaPropertyBuilder id(String id) {
        this.id = id;
        return this;
    }

    public SchemaPropertyBuilder name(String name) {
        this.name = name;
        return this;
    }

    public SchemaPropertyBuilder physicalName(String physicalName) {
        this.physicalName = physicalName;
        return this;
    }

    public SchemaPropertyBuilder physicalType(String physicalType) {
        this.physicalType = physicalType;
        return this;
    }

    public SchemaPropertyBuilder description(String description) {
        this.description = description;
        return this;
    }

    public SchemaPropertyBuilder businessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public SchemaPropertyBuilder logicalType(LogicalType logicalType) {
        this.logicalType = logicalType;
        return this;
    }

    public SchemaPropertyBuilder logicalTypeOptions(LogicalTypeOptions logicalTypeOptions) {
        this.logicalTypeOptions = logicalTypeOptions;
        return this;
    }

    public SchemaPropertyBuilder primaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public SchemaPropertyBuilder primaryKeyPosition(Integer primaryKeyPosition) {
        this.primaryKeyPosition = primaryKeyPosition;
        return this;
    }

    public SchemaPropertyBuilder required(Boolean required) {
        this.required = required;
        return this;
    }

    public SchemaPropertyBuilder unique(Boolean unique) {
        this.unique = unique;
        return this;
    }

    public SchemaPropertyBuilder partitioned(Boolean partitioned) {
        this.partitioned = partitioned;
        return this;
    }

    public SchemaPropertyBuilder partitionKeyPosition(Integer partitionKeyPosition) {
        this.partitionKeyPosition = partitionKeyPosition;
        return this;
    }

    public SchemaPropertyBuilder classification(String classification) {
        this.classification = classification;
        return this;
    }

    public SchemaPropertyBuilder encryptedName(String encryptedName) {
        this.encryptedName = encryptedName;
        return this;
    }

    public SchemaPropertyBuilder transformSourceObjects(List<String> transformSourceObjects) {
        this.transformSourceObjects = transformSourceObjects == null
                ? null : new ArrayList<>(transformSourceObjects);
        return this;
    }

    public SchemaPropertyBuilder transformLogic(String transformLogic) {
        this.transformLogic = transformLogic;
        return this;
    }

    public SchemaPropertyBuilder transformDescription(String transformDescription) {
        this.transformDescription = transformDescription;
        return this;
    }

    public SchemaPropertyBuilder examples(List<Object> examples) {
        this.examples = examples == null ? null : new ArrayList<>(examples);
        return this;
    }

    public SchemaPropertyBuilder criticalDataElement(Boolean criticalDataElement) {
        this.criticalDataElement = criticalDataElement;
        return this;
    }

    public SchemaPropertyBuilder items(Consumer<SchemaPropertyBuilder> config) {
        SchemaPropertyBuilder builder = new SchemaPropertyBuilder();
        config.accept(builder);
        this.items = builder.build();
        return this;
    }

    public SchemaPropertyBuilder items(SchemaProperty items) {
        this.items = items;
        return this;
    }

    public SchemaPropertyBuilder property(Consumer<SchemaPropertyBuilder> config) {
        SchemaPropertyBuilder builder = new SchemaPropertyBuilder();
        config.accept(builder);
        if (properties == null) {
            properties = new ArrayList<>();
        }
        properties.add(builder.build());
        return this;
    }

    public SchemaPropertyBuilder addProperty(SchemaProperty property) {
        if (properties == null) {
            properties = new ArrayList<>();
        }
        properties.add(property);
        return this;
    }

    public SchemaPropertyBuilder relationship(Consumer<RelationshipBuilder> config) {
        RelationshipBuilder builder = new RelationshipBuilder();
        config.accept(builder);
        if (relationships == null) {
            relationships = new ArrayList<>();
        }
        relationships.add(builder.build());
        return this;
    }

    public SchemaPropertyBuilder quality(Consumer<DataQualityBuilder> config) {
        DataQualityBuilder builder = new DataQualityBuilder();
        config.accept(builder);
        if (quality == null) {
            quality = new ArrayList<>();
        }
        quality.add(builder.build());
        return this;
    }

    public SchemaPropertyBuilder authoritativeDefinition(Consumer<AuthoritativeDefinitionBuilder> config) {
        AuthoritativeDefinitionBuilder builder = new AuthoritativeDefinitionBuilder();
        config.accept(builder);
        if (authoritativeDefinitions == null) {
            authoritativeDefinitions = new ArrayList<>();
        }
        authoritativeDefinitions.add(builder.build());
        return this;
    }

    public SchemaPropertyBuilder tag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
        return this;
    }

    public SchemaPropertyBuilder tags(List<String> tags) {
        this.tags = tags == null ? null : new ArrayList<>(tags);
        return this;
    }

    public SchemaPropertyBuilder tags(String... tags) {
        this.tags = tags == null ? null : new ArrayList<>(Arrays.asList(tags));
        return this;
    }

    public SchemaPropertyBuilder customProperty(Consumer<CustomPropertyBuilder> config) {
        CustomPropertyBuilder builder = new CustomPropertyBuilder();
        config.accept(builder);
        if (customProperties == null) {
            customProperties = new ArrayList<>();
        }
        customProperties.add(builder.build());
        return this;
    }

    public SchemaProperty build() {
        return new SchemaProperty(
                id, name, physicalName, physicalType, description, businessName,
                logicalType, logicalTypeOptions, primaryKey, primaryKeyPosition,
                required, unique, partitioned, partitionKeyPosition, classification,
                encryptedName, transformSourceObjects, transformLogic, transformDescription,
                examples, criticalDataElement, items, properties, relationships,
                authoritativeDefinitions, quality, tags, customProperties);
    }
}

package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.AuthoritativeDefinition;
import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;
import io.github.dataspeclabs.odcs.core.model.v3.DataQuality;
import io.github.dataspeclabs.odcs.core.model.v3.LogicalType;
import io.github.dataspeclabs.odcs.core.model.v3.Relationship;
import io.github.dataspeclabs.odcs.core.model.v3.SchemaObject;
import io.github.dataspeclabs.odcs.core.model.v3.SchemaProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link SchemaObject}.
 */
public final class SchemaObjectBuilder {

    private String id;
    private String name;
    private String physicalName;
    private String physicalType;
    private LogicalType logicalType;
    private String description;
    private String businessName;
    private String dataGranularityDescription;
    private List<SchemaProperty> properties;
    private List<Relationship> relationships;
    private List<AuthoritativeDefinition> authoritativeDefinitions;
    private List<DataQuality> quality;
    private List<String> tags;
    private List<CustomProperty> customProperties;

    public SchemaObjectBuilder id(String id) {
        this.id = id;
        return this;
    }

    public SchemaObjectBuilder name(String name) {
        this.name = name;
        return this;
    }

    public SchemaObjectBuilder physicalName(String physicalName) {
        this.physicalName = physicalName;
        return this;
    }

    public SchemaObjectBuilder physicalType(String physicalType) {
        this.physicalType = physicalType;
        return this;
    }

    public SchemaObjectBuilder logicalType(LogicalType logicalType) {
        this.logicalType = logicalType;
        return this;
    }

    public SchemaObjectBuilder description(String description) {
        this.description = description;
        return this;
    }

    public SchemaObjectBuilder businessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public SchemaObjectBuilder dataGranularityDescription(String dataGranularityDescription) {
        this.dataGranularityDescription = dataGranularityDescription;
        return this;
    }

    public SchemaObjectBuilder property(Consumer<SchemaPropertyBuilder> config) {
        SchemaPropertyBuilder builder = new SchemaPropertyBuilder();
        config.accept(builder);
        if (properties == null) {
            properties = new ArrayList<>();
        }
        properties.add(builder.build());
        return this;
    }

    public SchemaObjectBuilder addProperty(SchemaProperty property) {
        if (properties == null) {
            properties = new ArrayList<>();
        }
        properties.add(property);
        return this;
    }

    public SchemaObjectBuilder relationship(Consumer<RelationshipBuilder> config) {
        RelationshipBuilder builder = new RelationshipBuilder();
        config.accept(builder);
        if (relationships == null) {
            relationships = new ArrayList<>();
        }
        relationships.add(builder.build());
        return this;
    }

    public SchemaObjectBuilder quality(Consumer<DataQualityBuilder> config) {
        DataQualityBuilder builder = new DataQualityBuilder();
        config.accept(builder);
        if (quality == null) {
            quality = new ArrayList<>();
        }
        quality.add(builder.build());
        return this;
    }

    public SchemaObjectBuilder authoritativeDefinition(Consumer<AuthoritativeDefinitionBuilder> config) {
        AuthoritativeDefinitionBuilder builder = new AuthoritativeDefinitionBuilder();
        config.accept(builder);
        if (authoritativeDefinitions == null) {
            authoritativeDefinitions = new ArrayList<>();
        }
        authoritativeDefinitions.add(builder.build());
        return this;
    }

    public SchemaObjectBuilder tag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
        return this;
    }

    public SchemaObjectBuilder tags(List<String> tags) {
        this.tags = tags == null ? null : new ArrayList<>(tags);
        return this;
    }

    public SchemaObjectBuilder tags(String... tags) {
        this.tags = tags == null ? null : new ArrayList<>(Arrays.asList(tags));
        return this;
    }

    public SchemaObjectBuilder customProperty(Consumer<CustomPropertyBuilder> config) {
        CustomPropertyBuilder builder = new CustomPropertyBuilder();
        config.accept(builder);
        if (customProperties == null) {
            customProperties = new ArrayList<>();
        }
        customProperties.add(builder.build());
        return this;
    }

    public SchemaObject build() {
        return new SchemaObject(
                id, name, physicalName, physicalType, logicalType, description, businessName,
                dataGranularityDescription, properties, relationships, authoritativeDefinitions,
                quality, tags, customProperties);
    }
}

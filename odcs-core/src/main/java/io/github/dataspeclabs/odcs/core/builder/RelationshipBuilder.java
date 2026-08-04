package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;
import io.github.dataspeclabs.odcs.core.model.v3.Relationship;
import io.github.dataspeclabs.odcs.core.model.v3.RelationshipType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link Relationship}.
 */
public final class RelationshipBuilder {

    private RelationshipType type = RelationshipType.FOREIGN_KEY;
    private List<String> from;
    private List<String> to;
    private String description;
    private List<CustomProperty> customProperties;

    public RelationshipBuilder type(RelationshipType type) {
        this.type = type;
        return this;
    }

    public RelationshipBuilder from(String... from) {
        this.from = from == null ? null : new ArrayList<>(Arrays.asList(from));
        return this;
    }

    public RelationshipBuilder from(List<String> from) {
        this.from = from == null ? null : new ArrayList<>(from);
        return this;
    }

    public RelationshipBuilder to(String... to) {
        this.to = to == null ? null : new ArrayList<>(Arrays.asList(to));
        return this;
    }

    public RelationshipBuilder to(List<String> to) {
        this.to = to == null ? null : new ArrayList<>(to);
        return this;
    }

    public RelationshipBuilder description(String description) {
        this.description = description;
        return this;
    }

    public RelationshipBuilder customProperty(Consumer<CustomPropertyBuilder> config) {
        CustomPropertyBuilder builder = new CustomPropertyBuilder();
        config.accept(builder);
        if (customProperties == null) {
            customProperties = new ArrayList<>();
        }
        customProperties.add(builder.build());
        return this;
    }

    public Relationship build() {
        return new Relationship(type, from, to, description, customProperties);
    }
}

package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;

/**
 * Fluent builder for {@link CustomProperty}.
 */
public final class CustomPropertyBuilder {

    private String id;
    private String property;
    private Object value;
    private String description;

    public CustomPropertyBuilder id(String id) {
        this.id = id;
        return this;
    }

    public CustomPropertyBuilder property(String property) {
        this.property = property;
        return this;
    }

    public CustomPropertyBuilder value(Object value) {
        this.value = value;
        return this;
    }

    public CustomPropertyBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CustomProperty build() {
        return new CustomProperty(id, property, value, description);
    }
}

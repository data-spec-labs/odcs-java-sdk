package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.AuthoritativeDefinition;

/**
 * Fluent builder for {@link AuthoritativeDefinition}.
 */
public final class AuthoritativeDefinitionBuilder {

    private String url;
    private String type;
    private String description;

    public AuthoritativeDefinitionBuilder url(String url) {
        this.url = url;
        return this;
    }

    public AuthoritativeDefinitionBuilder type(String type) {
        this.type = type;
        return this;
    }

    public AuthoritativeDefinitionBuilder description(String description) {
        this.description = description;
        return this;
    }

    public AuthoritativeDefinition build() {
        return new AuthoritativeDefinition(url, type, description);
    }
}

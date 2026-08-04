package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.AuthoritativeDefinition;
import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;
import io.github.dataspeclabs.odcs.core.model.v3.Description;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link Description}.
 */
public final class DescriptionBuilder {

    private String purpose;
    private String limitations;
    private String usage;
    private List<AuthoritativeDefinition> authoritativeDefinitions;
    private List<CustomProperty> customProperties;

    public DescriptionBuilder purpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public DescriptionBuilder limitations(String limitations) {
        this.limitations = limitations;
        return this;
    }

    public DescriptionBuilder usage(String usage) {
        this.usage = usage;
        return this;
    }

    public DescriptionBuilder authoritativeDefinition(Consumer<AuthoritativeDefinitionBuilder> config) {
        AuthoritativeDefinitionBuilder builder = new AuthoritativeDefinitionBuilder();
        config.accept(builder);
        if (authoritativeDefinitions == null) {
            authoritativeDefinitions = new ArrayList<>();
        }
        authoritativeDefinitions.add(builder.build());
        return this;
    }

    public DescriptionBuilder customProperty(Consumer<CustomPropertyBuilder> config) {
        CustomPropertyBuilder builder = new CustomPropertyBuilder();
        config.accept(builder);
        if (customProperties == null) {
            customProperties = new ArrayList<>();
        }
        customProperties.add(builder.build());
        return this;
    }

    public Description build() {
        return new Description(purpose, limitations, usage, authoritativeDefinitions, customProperties);
    }
}

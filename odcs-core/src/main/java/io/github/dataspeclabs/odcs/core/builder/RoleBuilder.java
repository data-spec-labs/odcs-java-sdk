package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;
import io.github.dataspeclabs.odcs.core.model.v3.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link Role}.
 */
public final class RoleBuilder {

    private String role;
    private String access;
    private String firstLevelApprovers;
    private String secondLevelApprovers;
    private String description;
    private List<CustomProperty> customProperties;

    public RoleBuilder role(String role) {
        this.role = role;
        return this;
    }

    public RoleBuilder access(String access) {
        this.access = access;
        return this;
    }

    public RoleBuilder firstLevelApprovers(String firstLevelApprovers) {
        this.firstLevelApprovers = firstLevelApprovers;
        return this;
    }

    public RoleBuilder secondLevelApprovers(String secondLevelApprovers) {
        this.secondLevelApprovers = secondLevelApprovers;
        return this;
    }

    public RoleBuilder description(String description) {
        this.description = description;
        return this;
    }

    public RoleBuilder customProperty(Consumer<CustomPropertyBuilder> config) {
        CustomPropertyBuilder builder = new CustomPropertyBuilder();
        config.accept(builder);
        if (customProperties == null) {
            customProperties = new ArrayList<>();
        }
        customProperties.add(builder.build());
        return this;
    }

    public Role build() {
        return new Role(role, access, firstLevelApprovers, secondLevelApprovers, description, customProperties);
    }
}

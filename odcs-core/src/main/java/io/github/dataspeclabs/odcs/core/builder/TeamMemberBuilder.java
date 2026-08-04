package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;
import io.github.dataspeclabs.odcs.core.model.v3.TeamMember;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link TeamMember}.
 */
public final class TeamMemberBuilder {

    private String username;
    private String role;
    private String name;
    private String description;
    private LocalDate dateIn;
    private LocalDate dateOut;
    private String replacedByUsername;
    private List<CustomProperty> customProperties;

    public TeamMemberBuilder username(String username) {
        this.username = username;
        return this;
    }

    public TeamMemberBuilder role(String role) {
        this.role = role;
        return this;
    }

    public TeamMemberBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TeamMemberBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TeamMemberBuilder dateIn(LocalDate dateIn) {
        this.dateIn = dateIn;
        return this;
    }

    public TeamMemberBuilder dateOut(LocalDate dateOut) {
        this.dateOut = dateOut;
        return this;
    }

    public TeamMemberBuilder replacedByUsername(String replacedByUsername) {
        this.replacedByUsername = replacedByUsername;
        return this;
    }

    public TeamMemberBuilder customProperty(Consumer<CustomPropertyBuilder> config) {
        CustomPropertyBuilder builder = new CustomPropertyBuilder();
        config.accept(builder);
        if (customProperties == null) {
            customProperties = new ArrayList<>();
        }
        customProperties.add(builder.build());
        return this;
    }

    public TeamMember build() {
        return new TeamMember(
                username, role, name, description, dateIn, dateOut, replacedByUsername, customProperties);
    }
}

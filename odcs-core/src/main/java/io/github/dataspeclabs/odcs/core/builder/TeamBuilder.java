package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.Team;
import io.github.dataspeclabs.odcs.core.model.v3.TeamMember;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link Team}.
 */
public final class TeamBuilder {

    private String id;
    private String name;
    private String description;
    private List<TeamMember> members;

    public TeamBuilder id(String id) {
        this.id = id;
        return this;
    }

    public TeamBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TeamBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TeamBuilder member(Consumer<TeamMemberBuilder> config) {
        TeamMemberBuilder builder = new TeamMemberBuilder();
        config.accept(builder);
        if (members == null) {
            members = new ArrayList<>();
        }
        members.add(builder.build());
        return this;
    }

    public TeamBuilder addMember(TeamMember member) {
        if (members == null) {
            members = new ArrayList<>();
        }
        members.add(member);
        return this;
    }

    public Team build() {
        return new Team(id, name, description, members);
    }
}

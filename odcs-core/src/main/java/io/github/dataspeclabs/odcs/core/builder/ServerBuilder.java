package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.Server;
import io.github.dataspeclabs.odcs.core.model.v3.ServerType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fluent builder for {@link Server}.
 */
public final class ServerBuilder {

    private String server;
    private ServerType type;
    private String environment;
    private String description;
    private List<String> roles;
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    public ServerBuilder server(String server) {
        this.server = server;
        return this;
    }

    public ServerBuilder type(ServerType type) {
        this.type = type;
        return this;
    }

    public ServerBuilder environment(String environment) {
        this.environment = environment;
        return this;
    }

    public ServerBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ServerBuilder roles(List<String> roles) {
        this.roles = roles == null ? null : new ArrayList<>(roles);
        return this;
    }

    public ServerBuilder role(String role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(role);
        return this;
    }

    /**
     * Adds a type-specific property (e.g. host, port, database).
     */
    public ServerBuilder property(String name, Object value) {
        additionalProperties.put(name, value);
        return this;
    }

    public Server build() {
        return Server.create(server, type, environment, description, roles, additionalProperties);
    }
}

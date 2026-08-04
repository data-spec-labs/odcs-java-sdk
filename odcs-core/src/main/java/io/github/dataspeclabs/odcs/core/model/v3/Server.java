package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Physical server / infrastructure entry where contract data resides.
 * Type-specific fields (host, port, database, project, …) are captured via
 * {@link JsonAnySetter} rather than as 31 typed subclasses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Server {

    private final String server;
    private final ServerType type;
    private final String environment;
    private final String description;
    private final List<String> roles;
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonCreator
    public Server(
            @JsonProperty("server") String server,
            @JsonProperty("type") ServerType type,
            @JsonProperty("environment") String environment,
            @JsonProperty("description") String description,
            @JsonProperty("roles") List<String> roles
    ) {
        this.server = server;
        this.type = type;
        this.environment = environment;
        this.description = description;
        this.roles = roles == null ? null : List.copyOf(roles);
    }

    /**
     * Creates a server including type-specific additional properties (host, port, database, …).
     */
    public static Server create(
            String server,
            ServerType type,
            String environment,
            String description,
            List<String> roles,
            Map<String, Object> additionalProperties
    ) {
        Server result = new Server(server, type, environment, description, roles);
        if (additionalProperties != null) {
            result.additionalProperties.putAll(additionalProperties);
        }
        return result;
    }

    @JsonProperty("server")
    public String server() {
        return server;
    }

    @JsonProperty("type")
    public ServerType type() {
        return type;
    }

    @JsonProperty("environment")
    public String environment() {
        return environment;
    }

    @JsonProperty("description")
    public String description() {
        return description;
    }

    @JsonProperty("roles")
    public List<String> roles() {
        return roles;
    }

    @JsonAnySetter
    void putAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> additionalProperties() {
        return Collections.unmodifiableMap(additionalProperties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Server other)) {
            return false;
        }
        return Objects.equals(server, other.server)
                && type == other.type
                && Objects.equals(environment, other.environment)
                && Objects.equals(description, other.description)
                && Objects.equals(roles, other.roles)
                && Objects.equals(additionalProperties, other.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, type, environment, description, roles, additionalProperties);
    }

    @Override
    public String toString() {
        return "Server[server=" + server
                + ", type=" + type
                + ", environment=" + environment
                + ", description=" + description
                + ", roles=" + roles
                + ", additionalProperties=" + additionalProperties
                + "]";
    }
}

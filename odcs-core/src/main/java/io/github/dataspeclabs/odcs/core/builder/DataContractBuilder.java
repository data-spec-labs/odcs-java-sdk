package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.AuthoritativeDefinition;
import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;
import io.github.dataspeclabs.odcs.core.model.v3.DataContract;
import io.github.dataspeclabs.odcs.core.model.v3.Description;
import io.github.dataspeclabs.odcs.core.model.v3.Pricing;
import io.github.dataspeclabs.odcs.core.model.v3.Role;
import io.github.dataspeclabs.odcs.core.model.v3.SchemaObject;
import io.github.dataspeclabs.odcs.core.model.v3.Server;
import io.github.dataspeclabs.odcs.core.model.v3.SlaProperty;
import io.github.dataspeclabs.odcs.core.model.v3.SupportChannel;
import io.github.dataspeclabs.odcs.core.model.v3.Team;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link DataContract} (ODCS v3.x).
 */
public final class DataContractBuilder {

    private String apiVersion = "v3.1.0";
    private String kind = "DataContract";
    private String id;
    private String name;
    private String version;
    private String status;
    private String tenant;
    private String domain;
    @Deprecated
    private String dataProduct;
    private List<String> tags;
    private Description description;
    private List<AuthoritativeDefinition> authoritativeDefinitions;
    private List<SchemaObject> schema;
    private List<SupportChannel> support;
    private Pricing price;
    private Team team;
    private List<Role> roles;
    private List<SlaProperty> slaProperties;
    @Deprecated
    private String slaDefaultElement;
    private List<Server> servers;
    private List<CustomProperty> customProperties;
    private Instant contractCreatedTs;

    private DataContractBuilder() {
    }

    public static DataContractBuilder create() {
        return new DataContractBuilder();
    }

    /**
     * Seeds a builder from an existing contract for programmatic edits.
     */
    public static DataContractBuilder from(DataContract contract) {
        Objects.requireNonNull(contract, "contract");
        DataContractBuilder builder = new DataContractBuilder();
        builder.apiVersion = contract.apiVersion();
        builder.kind = contract.kind();
        builder.id = contract.id();
        builder.name = contract.name();
        builder.version = contract.version();
        builder.status = contract.status();
        builder.tenant = contract.tenant();
        builder.domain = contract.domain();
        builder.dataProduct = contract.dataProduct();
        builder.tags = copyList(contract.tags());
        builder.description = contract.description();
        builder.authoritativeDefinitions = copyList(contract.authoritativeDefinitions());
        builder.schema = copyList(contract.schema());
        builder.support = copyList(contract.support());
        builder.price = contract.price();
        builder.team = contract.team();
        builder.roles = copyList(contract.roles());
        builder.slaProperties = copyList(contract.slaProperties());
        builder.slaDefaultElement = contract.slaDefaultElement();
        builder.servers = copyList(contract.servers());
        builder.customProperties = copyList(contract.customProperties());
        builder.contractCreatedTs = contract.contractCreatedTs();
        return builder;
    }

    public DataContractBuilder apiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    public DataContractBuilder kind(String kind) {
        this.kind = kind;
        return this;
    }

    public DataContractBuilder id(String id) {
        this.id = id;
        return this;
    }

    public DataContractBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DataContractBuilder version(String version) {
        this.version = version;
        return this;
    }

    public DataContractBuilder status(String status) {
        this.status = status;
        return this;
    }

    public DataContractBuilder tenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    public DataContractBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    @Deprecated
    public DataContractBuilder dataProduct(String dataProduct) {
        this.dataProduct = dataProduct;
        return this;
    }

    public DataContractBuilder tag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
        return this;
    }

    public DataContractBuilder tags(List<String> tags) {
        this.tags = tags == null ? null : new ArrayList<>(tags);
        return this;
    }

    public DataContractBuilder tags(String... tags) {
        this.tags = tags == null ? null : new ArrayList<>(Arrays.asList(tags));
        return this;
    }

    public DataContractBuilder description(Consumer<DescriptionBuilder> config) {
        DescriptionBuilder builder = new DescriptionBuilder();
        config.accept(builder);
        this.description = builder.build();
        return this;
    }

    public DataContractBuilder description(Description description) {
        this.description = description;
        return this;
    }

    public DataContractBuilder authoritativeDefinition(Consumer<AuthoritativeDefinitionBuilder> config) {
        AuthoritativeDefinitionBuilder builder = new AuthoritativeDefinitionBuilder();
        config.accept(builder);
        if (authoritativeDefinitions == null) {
            authoritativeDefinitions = new ArrayList<>();
        }
        authoritativeDefinitions.add(builder.build());
        return this;
    }

    public DataContractBuilder schemaObject(Consumer<SchemaObjectBuilder> config) {
        SchemaObjectBuilder builder = new SchemaObjectBuilder();
        config.accept(builder);
        if (schema == null) {
            schema = new ArrayList<>();
        }
        schema.add(builder.build());
        return this;
    }

    public DataContractBuilder addSchemaObject(SchemaObject schemaObject) {
        if (schema == null) {
            schema = new ArrayList<>();
        }
        schema.add(schemaObject);
        return this;
    }

    public DataContractBuilder server(Consumer<ServerBuilder> config) {
        ServerBuilder builder = new ServerBuilder();
        config.accept(builder);
        if (servers == null) {
            servers = new ArrayList<>();
        }
        servers.add(builder.build());
        return this;
    }

    public DataContractBuilder addServer(Server server) {
        if (servers == null) {
            servers = new ArrayList<>();
        }
        servers.add(server);
        return this;
    }

    public DataContractBuilder team(Consumer<TeamBuilder> config) {
        TeamBuilder builder = new TeamBuilder();
        config.accept(builder);
        this.team = builder.build();
        return this;
    }

    public DataContractBuilder team(Team team) {
        this.team = team;
        return this;
    }

    public DataContractBuilder role(Consumer<RoleBuilder> config) {
        RoleBuilder builder = new RoleBuilder();
        config.accept(builder);
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(builder.build());
        return this;
    }

    public DataContractBuilder support(Consumer<SupportChannelBuilder> config) {
        SupportChannelBuilder builder = new SupportChannelBuilder();
        config.accept(builder);
        if (support == null) {
            support = new ArrayList<>();
        }
        support.add(builder.build());
        return this;
    }

    public DataContractBuilder slaProperty(Consumer<SlaPropertyBuilder> config) {
        SlaPropertyBuilder builder = new SlaPropertyBuilder();
        config.accept(builder);
        if (slaProperties == null) {
            slaProperties = new ArrayList<>();
        }
        slaProperties.add(builder.build());
        return this;
    }

    @Deprecated
    public DataContractBuilder slaDefaultElement(String slaDefaultElement) {
        this.slaDefaultElement = slaDefaultElement;
        return this;
    }

    public DataContractBuilder price(Consumer<PricingBuilder> config) {
        PricingBuilder builder = new PricingBuilder();
        config.accept(builder);
        this.price = builder.build();
        return this;
    }

    public DataContractBuilder price(Pricing price) {
        this.price = price;
        return this;
    }

    public DataContractBuilder customProperty(Consumer<CustomPropertyBuilder> config) {
        CustomPropertyBuilder builder = new CustomPropertyBuilder();
        config.accept(builder);
        if (customProperties == null) {
            customProperties = new ArrayList<>();
        }
        customProperties.add(builder.build());
        return this;
    }

    public DataContractBuilder contractCreatedTs(Instant contractCreatedTs) {
        this.contractCreatedTs = contractCreatedTs;
        return this;
    }

    /**
     * Builds an immutable {@link DataContract}.
     *
     * @throws IllegalStateException if required root fields are blank
     */
    public DataContract build() {
        requireNonBlank("apiVersion", apiVersion);
        requireNonBlank("kind", kind);
        requireNonBlank("id", id);
        requireNonBlank("version", version);
        requireNonBlank("status", status);
        return new DataContract(
                apiVersion, kind, id, name, version, status, tenant, domain, dataProduct,
                tags, description, authoritativeDefinitions, schema, support, price, team,
                roles, slaProperties, slaDefaultElement, servers, customProperties, contractCreatedTs);
    }

    private static void requireNonBlank(String field, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required field '" + field + "' must not be blank");
        }
    }

    private static <T> List<T> copyList(List<T> source) {
        return source == null ? null : new ArrayList<>(source);
    }
}

package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

/**
 * Root ODCS v3.x data contract model.
 * Covers apiVersion v3.0.0 through v3.1.0 (and additive future v3.x).
 * Strict semantic validation is deferred to ODCSSpecValidator.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DataContract(
        @JsonProperty("apiVersion") String apiVersion,
        @JsonProperty("kind") String kind,
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("version") String version,
        @JsonProperty("status") String status,
        @JsonProperty("tenant") String tenant,
        @JsonProperty("domain") String domain,
        @JsonProperty("dataProduct") @Deprecated String dataProduct,
        @JsonProperty("tags") List<String> tags,
        @JsonProperty("description") Description description,
        @JsonProperty("authoritativeDefinitions") List<AuthoritativeDefinition> authoritativeDefinitions,
        @JsonProperty("schema") List<SchemaObject> schema,
        @JsonProperty("support") List<SupportChannel> support,
        @JsonProperty("price") Pricing price,
        @JsonProperty("team") Team team,
        @JsonProperty("roles") List<Role> roles,
        @JsonProperty("slaProperties") List<SlaProperty> slaProperties,
        @JsonProperty("slaDefaultElement") @Deprecated String slaDefaultElement,
        @JsonProperty("servers") List<Server> servers,
        @JsonProperty("customProperties") List<CustomProperty> customProperties,
        @JsonProperty("contractCreatedTs") Instant contractCreatedTs
) {
    @JsonCreator
    public DataContract {
    }
}

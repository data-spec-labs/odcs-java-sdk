package io.github.dataspeclabs.odcs.core.internal;

import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SchemaRegistryConfig;
import com.networknt.schema.SpecificationVersion;
import io.github.dataspeclabs.odcs.core.OdcsException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves and caches Bitol ODCS JSON Schemas by {@code apiVersion}.
 */
public final class OdcsSchemaRegistry {

    public static final String LATEST_VERSION = "v3.1.0";

    private static final Map<String, String> API_VERSION_TO_SCHEMA_FILE = Map.ofEntries(
            Map.entry("v3.1.0", "odcs/schemas/odcs-json-schema-v3.1.0.json"),
            Map.entry("v3.0.2", "odcs/schemas/odcs-json-schema-v3.0.2.json"),
            Map.entry("v3.0.1", "odcs/schemas/odcs-json-schema-v3.0.2.json"),
            Map.entry("v3.0.0", "odcs/schemas/odcs-json-schema-v3.0.0.json"),
            Map.entry("v2.2.2", "odcs/schemas/odcs-json-schema-v2.2.2.json"),
            Map.entry("v2.2.1", "odcs/schemas/odcs-json-schema-v2.2.2.json"),
            Map.entry("v2.2.0", "odcs/schemas/odcs-json-schema-v2.2.2.json")
    );

    private static final SchemaRegistry NETWORKNT_REGISTRY = createRegistry();
    private static final ConcurrentHashMap<String, Schema> SCHEMA_CACHE = new ConcurrentHashMap<>();

    private OdcsSchemaRegistry() {
    }

    /**
     * Resolves which bundled schema version to use for the given {@code apiVersion}.
     */
    public static ResolvedSchema resolve(String apiVersion) {
        if (apiVersion == null || apiVersion.isBlank()) {
            return new ResolvedSchema(
                    LATEST_VERSION,
                    loadSchema(LATEST_VERSION),
                    "No apiVersion found — validating against latest (" + LATEST_VERSION + ").");
        }
        if (API_VERSION_TO_SCHEMA_FILE.containsKey(apiVersion)) {
            String cacheKey = canonicalKey(apiVersion);
            return new ResolvedSchema(apiVersion, loadSchema(cacheKey), null);
        }
        return new ResolvedSchema(
                LATEST_VERSION,
                loadSchema(LATEST_VERSION),
                "Unknown apiVersion \"" + apiVersion + "\" — validating against latest ("
                        + LATEST_VERSION + ").");
    }

    private static String canonicalKey(String apiVersion) {
        // Map patch aliases to the file's "primary" version key used for caching.
        return switch (apiVersion) {
            case "v3.0.1" -> "v3.0.2";
            case "v2.2.1", "v2.2.0" -> "v2.2.2";
            default -> apiVersion;
        };
    }

    private static Schema loadSchema(String versionKey) {
        return SCHEMA_CACHE.computeIfAbsent(versionKey, OdcsSchemaRegistry::loadSchemaUncached);
    }

    private static Schema loadSchemaUncached(String versionKey) {
        String resource = API_VERSION_TO_SCHEMA_FILE.get(versionKey);
        Objects.requireNonNull(resource, "No schema resource for version: " + versionKey);
        try (InputStream in = OdcsSchemaRegistry.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new OdcsException("Missing classpath ODCS schema resource: " + resource);
            }
            return NETWORKNT_REGISTRY.getSchema(in, InputFormat.JSON);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load ODCS schema: " + resource, e);
        }
    }

    private static SchemaRegistry createRegistry() {
        SchemaRegistryConfig config = SchemaRegistryConfig.builder()
                .formatAssertionsEnabled(true)
                .build();
        return SchemaRegistry.withDefaultDialect(
                SpecificationVersion.DRAFT_2019_09,
                builder -> builder.schemaRegistryConfig(config));
    }

    /**
     * Result of resolving an apiVersion to a cached {@link Schema}.
     */
    public record ResolvedSchema(String resolvedVersion, Schema schema, String versionWarning) {
    }
}

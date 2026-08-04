package io.github.dataspeclabs.odcs.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.Error;
import com.networknt.schema.path.NodePath;
import io.github.dataspeclabs.odcs.core.internal.OdcsObjectMappers;
import io.github.dataspeclabs.odcs.core.internal.OdcsSchemaRegistry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates ODCS YAML/JSON documents against official Bitol JSON Schemas.
 * Operates on untyped {@link JsonNode} trees (supports v2.x schema checks without model.v2).
 */
public final class ODCSSpecValidator {

    private ODCSSpecValidator() {
    }

    /**
     * Validates contract content, auto-detecting YAML vs JSON.
     */
    public static SpecValidationReport validate(String content) {
        return validate(content, ODCSParser.detectFormat(content));
    }

    /**
     * Validates contract content using the given format.
     */
    public static SpecValidationReport validate(String content, OdcsFormat format) {
        if (content == null || content.isBlank()) {
            throw new OdcsParseException("Contract content must not be null or blank");
        }
        if (format == null) {
            throw new OdcsParseException("Format must not be null");
        }
        try {
            JsonNode node = OdcsObjectMappers.forFormat(format).readTree(content);
            return validateNode(node);
        } catch (OdcsParseException e) {
            throw e;
        } catch (IOException e) {
            throw new OdcsParseException("Failed to parse ODCS document for validation", e);
        }
    }

    /**
     * Validates a contract file; format is inferred from the file extension.
     */
    public static SpecValidationReport validate(File file) {
        if (file == null) {
            throw new OdcsParseException("File must not be null");
        }
        return validate(file.toPath());
    }

    /**
     * Validates a contract path; format is inferred from the file extension.
     */
    public static SpecValidationReport validate(Path path) {
        if (path == null) {
            throw new OdcsParseException("Path must not be null");
        }
        OdcsFormat format = ODCSParser.detectFormat(path);
        try (InputStream in = Files.newInputStream(path)) {
            return validate(in, format);
        } catch (OdcsParseException e) {
            throw e;
        } catch (IOException e) {
            throw new OdcsParseException("Failed to read ODCS document from path: " + path, e);
        }
    }

    /**
     * Validates a contract from an input stream using an explicit format.
     * The stream is not closed by this method.
     */
    public static SpecValidationReport validate(InputStream in, OdcsFormat format) {
        if (in == null) {
            throw new OdcsParseException("InputStream must not be null");
        }
        if (format == null) {
            throw new OdcsParseException("Format must not be null");
        }
        try {
            JsonNode node = OdcsObjectMappers.forFormat(format).readTree(in);
            return validateNode(node);
        } catch (OdcsParseException e) {
            throw e;
        } catch (IOException e) {
            throw new OdcsParseException("Failed to parse ODCS document from input stream", e);
        }
    }

    static SpecValidationReport validateNode(JsonNode node) {
        if (node == null || node.isNull()) {
            throw new OdcsParseException("Contract document must not be null");
        }
        String apiVersion = readApiVersion(node);
        OdcsSchemaRegistry.ResolvedSchema resolved = OdcsSchemaRegistry.resolve(apiVersion);
        List<Error> messages = resolved.schema().validate(node);
        List<SpecValidationError> errors = new ArrayList<>(messages.size());
        for (Error message : messages) {
            errors.add(new SpecValidationError(
                    prettifyPath(message.getInstanceLocation()),
                    message.getMessage() == null ? "Validation error" : message.getMessage(),
                    message.getKeyword() == null ? "" : message.getKeyword()));
        }
        return SpecValidationReport.of(errors, resolved.resolvedVersion(), resolved.versionWarning());
    }

    private static String readApiVersion(JsonNode node) {
        JsonNode apiVersion = node.get("apiVersion");
        if (apiVersion == null || apiVersion.isNull() || !apiVersion.isTextual()) {
            return null;
        }
        String value = apiVersion.asText();
        return value.isBlank() ? null : value;
    }

    /**
     * Converts a networknt instance path to a human-readable form
     * ({@code /schema/0/name} → {@code schema[0].name}).
     */
    static String prettifyPath(NodePath instanceLocation) {
        if (instanceLocation == null) {
            return "(root)";
        }
        String raw = instanceLocation.toString();
        if (raw == null || raw.isBlank() || "/".equals(raw)) {
            return "(root)";
        }
        String path = raw.startsWith("/") ? raw.substring(1) : raw;
        if (path.isEmpty()) {
            return "(root)";
        }
        return path
                .replaceAll("/(\\d+)", "[$1]")
                .replace('/', '.');
    }
}

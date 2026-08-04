package io.github.dataspeclabs.odcs.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dataspeclabs.odcs.core.internal.OdcsObjectMappers;
import io.github.dataspeclabs.odcs.core.model.v3.DataContract;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Parses ODCS contracts from YAML or JSON into {@link DataContract}.
 * Supports the v3.x model family only; full JSON Schema validation is separate.
 */
public final class ODCSParser {

    private ODCSParser() {
    }

    /**
     * Parses contract content, auto-detecting YAML vs JSON.
     */
    public static DataContract parse(String content) {
        return parse(content, detectFormat(content));
    }

    /**
     * Parses contract content using the given format.
     */
    public static DataContract parse(String content, OdcsFormat format) {
        if (content == null || content.isBlank()) {
            throw new OdcsParseException("Contract content must not be null or blank");
        }
        if (format == null) {
            throw new OdcsParseException("Format must not be null");
        }
        try {
            DataContract contract = OdcsObjectMappers.forFormat(format)
                    .readValue(content, DataContract.class);
            return assertSupportedApiVersion(contract);
        } catch (OdcsParseException e) {
            throw e;
        } catch (IOException e) {
            throw new OdcsParseException("Failed to parse ODCS contract from string", e);
        }
    }

    /**
     * Parses a contract file; format is inferred from the file extension.
     */
    public static DataContract parse(File file) {
        if (file == null) {
            throw new OdcsParseException("File must not be null");
        }
        return parse(file.toPath());
    }

    /**
     * Parses a contract path; format is inferred from the file extension.
     */
    public static DataContract parse(Path path) {
        if (path == null) {
            throw new OdcsParseException("Path must not be null");
        }
        OdcsFormat format = detectFormat(path);
        try (InputStream in = Files.newInputStream(path)) {
            return parse(in, format);
        } catch (OdcsParseException e) {
            throw e;
        } catch (IOException e) {
            throw new OdcsParseException("Failed to parse ODCS contract from path: " + path, e);
        }
    }

    /**
     * Parses a contract from an input stream using an explicit format.
     * The stream is not closed by this method.
     */
    public static DataContract parse(InputStream in, OdcsFormat format) {
        if (in == null) {
            throw new OdcsParseException("InputStream must not be null");
        }
        if (format == null) {
            throw new OdcsParseException("Format must not be null");
        }
        try {
            ObjectMapper mapper = OdcsObjectMappers.forFormat(format);
            DataContract contract = mapper.readValue(in, DataContract.class);
            return assertSupportedApiVersion(contract);
        } catch (OdcsParseException e) {
            throw e;
        } catch (IOException e) {
            throw new OdcsParseException("Failed to parse ODCS contract from input stream", e);
        }
    }

    static OdcsFormat detectFormat(String content) {
        if (content == null) {
            return OdcsFormat.YAML;
        }
        String trimmed = content.stripLeading();
        if (trimmed.isEmpty()) {
            return OdcsFormat.YAML;
        }
        char first = trimmed.charAt(0);
        if (first == '{' || first == '[') {
            return OdcsFormat.JSON;
        }
        return OdcsFormat.YAML;
    }

    static OdcsFormat detectFormat(Path path) {
        String name = path.getFileName() != null ? path.getFileName().toString().toLowerCase() : "";
        if (name.endsWith(".json")) {
            return OdcsFormat.JSON;
        }
        return OdcsFormat.YAML;
    }

    static DataContract assertSupportedApiVersion(DataContract contract) {
        if (contract == null) {
            throw new OdcsParseException("Parsed contract was null");
        }
        String apiVersion = contract.apiVersion();
        if (apiVersion == null || apiVersion.isBlank()) {
            throw new OdcsParseException(
                    "Missing required apiVersion. ODCSParser currently supports v3.x contracts only.");
        }
        if (!apiVersion.startsWith("v3.")) {
            throw new OdcsParseException(
                    "Unsupported apiVersion '" + apiVersion
                            + "'. ODCSParser currently supports v3.x only; "
                            + "use ODCSMigrator (future) for legacy versions.");
        }
        return contract;
    }
}

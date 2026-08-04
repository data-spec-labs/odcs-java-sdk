package io.github.dataspeclabs.odcs.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dataspeclabs.odcs.core.internal.OdcsObjectMappers;
import io.github.dataspeclabs.odcs.core.model.v3.DataContract;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Serializes {@link DataContract} instances to formatted YAML or JSON.
 */
public final class ODCSWriter {

    private ODCSWriter() {
    }

    /**
     * Serializes the contract to a YAML string.
     */
    public static String toYaml(DataContract contract) {
        return writeToString(contract, OdcsFormat.YAML);
    }

    /**
     * Serializes the contract to a pretty-printed JSON string.
     */
    public static String toJson(DataContract contract) {
        return writeToString(contract, OdcsFormat.JSON);
    }

    /**
     * Writes the contract to a file in the given format.
     */
    public static void write(DataContract contract, File file, OdcsFormat format) {
        if (file == null) {
            throw new OdcsParseException("File must not be null");
        }
        write(contract, file.toPath(), format);
    }

    /**
     * Writes the contract to a path in the given format.
     */
    public static void write(DataContract contract, Path path, OdcsFormat format) {
        if (path == null) {
            throw new OdcsParseException("Path must not be null");
        }
        requireContract(contract);
        requireFormat(format);
        try (OutputStream out = Files.newOutputStream(path)) {
            write(contract, out, format);
        } catch (OdcsParseException e) {
            throw e;
        } catch (IOException e) {
            throw new OdcsParseException("Failed to write ODCS contract to path: " + path, e);
        }
    }

    /**
     * Writes the contract to an output stream in the given format.
     * The stream is not closed by this method.
     */
    public static void write(DataContract contract, OutputStream out, OdcsFormat format) {
        requireContract(contract);
        if (out == null) {
            throw new OdcsParseException("OutputStream must not be null");
        }
        requireFormat(format);
        try {
            ObjectMapper mapper = OdcsObjectMappers.forFormat(format);
            mapper.writeValue(out, contract);
        } catch (IOException e) {
            throw new OdcsParseException("Failed to write ODCS contract to output stream", e);
        }
    }

    private static String writeToString(DataContract contract, OdcsFormat format) {
        requireContract(contract);
        try {
            return OdcsObjectMappers.forFormat(format).writeValueAsString(contract);
        } catch (JsonProcessingException e) {
            throw new OdcsParseException("Failed to serialize ODCS contract to " + format, e);
        }
    }

    private static void requireContract(DataContract contract) {
        if (contract == null) {
            throw new OdcsParseException("DataContract must not be null");
        }
    }

    private static void requireFormat(OdcsFormat format) {
        if (format == null) {
            throw new OdcsParseException("Format must not be null");
        }
    }
}

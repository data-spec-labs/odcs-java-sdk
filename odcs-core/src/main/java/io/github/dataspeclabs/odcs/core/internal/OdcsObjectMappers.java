package io.github.dataspeclabs.odcs.core.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.dataspeclabs.odcs.core.OdcsFormat;

/**
 * Shared Jackson {@link ObjectMapper} instances for ODCS YAML/JSON I/O.
 */
public final class OdcsObjectMappers {

    private static final ObjectMapper YAML_MAPPER = createYamlMapper();
    private static final ObjectMapper JSON_MAPPER = createJsonMapper();

    private OdcsObjectMappers() {
    }

    public static ObjectMapper forFormat(OdcsFormat format) {
        return format == OdcsFormat.JSON ? JSON_MAPPER : YAML_MAPPER;
    }

    public static ObjectMapper yaml() {
        return YAML_MAPPER;
    }

    public static ObjectMapper json() {
        return JSON_MAPPER;
    }

    private static ObjectMapper createYamlMapper() {
        YAMLFactory factory = new YAMLFactory()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        ObjectMapper mapper = new ObjectMapper(factory);
        configure(mapper);
        return mapper;
    }

    private static ObjectMapper createJsonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        configure(mapper);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    private static void configure(ObjectMapper mapper) {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}

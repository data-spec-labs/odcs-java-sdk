package io.github.dataspeclabs.odcs.spark.schema;

import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;
import io.github.dataspeclabs.odcs.core.model.v3.LogicalType;
import io.github.dataspeclabs.odcs.core.model.v3.SchemaProperty;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.MetadataBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Codec for ODCS provenance stored on Spark {@link org.apache.spark.sql.types.StructField} metadata.
 */
public final class OdcsMetadata {

    public static final String COMMENT = "comment";
    public static final String NAME = "odcs.name";
    public static final String LOGICAL_TYPE = "odcs.logicalType";
    public static final String PHYSICAL_TYPE = "odcs.physicalType";
    public static final String PRIMARY_KEY = "odcs.primaryKey";
    public static final String PRIMARY_KEY_POSITION = "odcs.primaryKeyPosition";
    public static final String CLASSIFICATION = "odcs.classification";
    public static final String CRITICAL_DATA_ELEMENT = "odcs.criticalDataElement";
    public static final String TAGS = "odcs.tags";
    public static final String MAP_KEY_TYPE = "odcs.mapKeyType";
    public static final String MAP_VALUE_TYPE = "odcs.mapValueType";
    public static final String MAP_VALUE_REQUIRED = "odcs.mapValueRequired";

    private OdcsMetadata() {
    }

    public static Metadata build(SchemaProperty property, String sparkFieldName, boolean usedPhysicalName) {
        MetadataBuilder builder = new MetadataBuilder();

        if (property.description() != null && !property.description().isBlank()) {
            builder.putString(COMMENT, property.description());
        }
        if (usedPhysicalName && property.name() != null) {
            builder.putString(NAME, property.name());
        }
        if (property.logicalType() != null) {
            builder.putString(LOGICAL_TYPE, property.logicalType().value());
        }
        if (property.physicalType() != null) {
            builder.putString(PHYSICAL_TYPE, property.physicalType());
        }
        if (property.primaryKey() != null) {
            builder.putBoolean(PRIMARY_KEY, property.primaryKey());
        }
        if (property.primaryKeyPosition() != null) {
            builder.putLong(PRIMARY_KEY_POSITION, property.primaryKeyPosition());
        }
        if (property.classification() != null) {
            builder.putString(CLASSIFICATION, property.classification());
        }
        if (property.criticalDataElement() != null) {
            builder.putBoolean(CRITICAL_DATA_ELEMENT, property.criticalDataElement());
        }
        if (property.tags() != null && !property.tags().isEmpty()) {
            builder.putStringArray(TAGS, property.tags().toArray(String[]::new));
        }

        customString(property, MapTypeCodec.MAP_KEY_TYPE)
                .ifPresent(v -> builder.putString(MAP_KEY_TYPE, v));
        customString(property, MapTypeCodec.MAP_VALUE_TYPE)
                .ifPresent(v -> builder.putString(MAP_VALUE_TYPE, v));
        customString(property, MapTypeCodec.MAP_VALUE_REQUIRED)
                .ifPresent(v -> builder.putString(MAP_VALUE_REQUIRED, v));

        return builder.build();
    }

    public static boolean hasOdcsKeys(Metadata metadata) {
        if (metadata == null) {
            return false;
        }
        return metadata.contains(LOGICAL_TYPE)
                || metadata.contains(PHYSICAL_TYPE)
                || metadata.contains(NAME)
                || metadata.contains(MAP_KEY_TYPE);
    }

    public static Optional<String> getString(Metadata metadata, String key) {
        if (metadata == null || !metadata.contains(key)) {
            return Optional.empty();
        }
        return Optional.ofNullable(metadata.getString(key));
    }

    public static Optional<Boolean> getBoolean(Metadata metadata, String key) {
        if (metadata == null || !metadata.contains(key)) {
            return Optional.empty();
        }
        return Optional.of(metadata.getBoolean(key));
    }

    public static Optional<Integer> getInt(Metadata metadata, String key) {
        if (metadata == null || !metadata.contains(key)) {
            return Optional.empty();
        }
        return Optional.of((int) metadata.getLong(key));
    }

    public static List<String> getTags(Metadata metadata) {
        if (metadata == null || !metadata.contains(TAGS)) {
            return List.of();
        }
        String[] arr = metadata.getStringArray(TAGS);
        if (arr == null || arr.length == 0) {
            return List.of();
        }
        return Collections.unmodifiableList(new ArrayList<>(Arrays.asList(arr)));
    }

    public static Optional<LogicalType> getLogicalType(Metadata metadata) {
        return getString(metadata, LOGICAL_TYPE).map(LogicalType::fromValue);
    }

    public static Optional<String> customString(SchemaProperty property, String key) {
        if (property.customProperties() == null) {
            return Optional.empty();
        }
        for (CustomProperty cp : property.customProperties()) {
            if (cp != null && key.equals(cp.property()) && cp.value() != null) {
                return Optional.of(String.valueOf(cp.value()));
            }
        }
        return Optional.empty();
    }
}

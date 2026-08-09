package io.github.dataspeclabs.odcs.spark.schema;

import io.github.dataspeclabs.odcs.core.model.v3.LogicalType;
import io.github.dataspeclabs.odcs.core.model.v3.SchemaObject;
import io.github.dataspeclabs.odcs.core.model.v3.SchemaProperty;
import io.github.dataspeclabs.odcs.spark.ConversionReport;
import io.github.dataspeclabs.odcs.spark.SparkSchemaOptions;
import io.github.dataspeclabs.odcs.spark.TypeMappingException;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Converts ODCS schema definitions to Spark {@link StructType} / {@link DataType}.
 */
public final class OdcsToSparkConverter {

    private final SparkSchemaOptions options;
    private final ConversionReport.Builder report;

    public OdcsToSparkConverter(SparkSchemaOptions options, ConversionReport.Builder report) {
        this.options = options;
        this.report = report;
    }

    public StructType toStructType(SchemaObject object) {
        if (object == null) {
            throw new TypeMappingException("", "SchemaObject must not be null");
        }
        String root = object.name() != null ? object.name() : "";
        return propertiesToStruct(object.properties(), root);
    }

    public StructField toStructField(SchemaProperty property) {
        return toStructField(property, propertyPath(null, property));
    }

    public DataType toDataType(SchemaProperty property) {
        return resolveDataType(property, propertyPath(null, property));
    }

    StructType propertiesToStruct(List<SchemaProperty> properties, String path) {
        if (properties == null || properties.isEmpty()) {
            failOrWarn(path, "object/schema has no properties", new StructType());
            return new StructType();
        }
        List<StructField> fields = new ArrayList<>(properties.size());
        for (SchemaProperty property : properties) {
            fields.add(toStructField(property, propertyPath(path, property)));
        }
        return new StructType(fields.toArray(StructField[]::new));
    }

    private StructField toStructField(SchemaProperty property, String path) {
        if (property == null) {
            throw new TypeMappingException(path, "SchemaProperty must not be null");
        }
        String fieldName = resolveFieldName(property, path);
        boolean usedPhysicalName = options.fieldNameSource() == SparkSchemaOptions.FieldNameSource.PHYSICAL_THEN_LOGICAL
                && property.physicalName() != null
                && !property.physicalName().isBlank();

        DataType dataType = resolveDataType(property, path);
        boolean nullable = !Boolean.TRUE.equals(property.required());

        Metadata metadata = options.emitMetadata()
                ? OdcsMetadata.build(property, fieldName, usedPhysicalName)
                : Metadata.empty();

        return new StructField(fieldName, dataType, nullable, metadata);
    }

    private DataType resolveDataType(SchemaProperty property, String path) {
        // 1. Map codec
        if (MapTypeCodec.isMapProperty(property)) {
            return MapTypeCodec.toMapType(
                    property,
                    options,
                    path,
                    report,
                    this::propertiesToStruct);
        }

        LogicalType logicalType = property.logicalType();

        // Nested object / array before physicalType scalar parse
        if (logicalType == LogicalType.ARRAY) {
            return resolveArray(property, path);
        }
        if (logicalType == LogicalType.OBJECT) {
            return propertiesToStruct(property.properties(), path);
        }

        // 2. physicalType refinement that fully determines the type
        Optional<DataType> fromPhysical = tryPhysicalRefinement(property, path);
        if (fromPhysical.isPresent()) {
            return fromPhysical.get();
        }

        // 3–4. logicalType + options / defaults
        if (logicalType == null) {
            failOrWarn(path, "missing logicalType", DataTypes.StringType);
            return DataTypes.StringType;
        }

        return TypeMappings.fromLogicalType(
                logicalType,
                property.logicalTypeOptions(),
                property.physicalType(),
                options,
                path,
                report);
    }

    private Optional<DataType> tryPhysicalRefinement(SchemaProperty property, String path) {
        String physical = property.physicalType();
        if (physical == null || physical.isBlank()) {
            return Optional.empty();
        }

        Optional<PhysicalTypeParser.DecimalSpec> decimal = PhysicalTypeParser.parseDecimal(physical);
        if (decimal.isPresent()) {
            PhysicalTypeParser.DecimalSpec d = decimal.get();
            if (d.isBare()) {
                if (options.requireDecimalPrecision()) {
                    failOrWarn(path, "decimal physicalType requires precision and scale, e.g. decimal(18,2)",
                            DataTypes.createDecimalType(10, 0));
                    return Optional.of(DataTypes.createDecimalType(10, 0));
                }
                return Optional.of(DataTypes.createDecimalType(10, 0));
            }
            return Optional.of(DataTypes.createDecimalType(d.precision(), d.scale()));
        }

        String p = physical.trim().toLowerCase(Locale.ROOT);
        return switch (p) {
            case "timestamp_ntz", "timestampntz" -> Optional.of(DataTypes.TimestampNTZType);
            case "timestamp", "datetime" ->
                    property.logicalType() == LogicalType.TIMESTAMP
                            ? Optional.of(DataTypes.TimestampType)
                            : Optional.empty();
            case "binary", "bytes", "bytea" -> Optional.of(DataTypes.BinaryType);
            case "tinyint", "byte" -> Optional.of(DataTypes.ByteType);
            case "smallint", "short" -> Optional.of(DataTypes.ShortType);
            case "int", "integer" -> Optional.of(DataTypes.IntegerType);
            case "bigint", "long" -> Optional.of(DataTypes.LongType);
            case "float", "real" -> Optional.of(DataTypes.FloatType);
            case "double" -> Optional.of(DataTypes.DoubleType);
            default -> {
                if ((property.logicalType() == null || property.logicalType() == LogicalType.STRING)
                        && PhysicalTypeParser.parseVarcharLength(physical).isPresent()) {
                    yield Optional.of(DataTypes.StringType);
                }
                yield Optional.empty();
            }
        };
    }

    private DataType resolveArray(SchemaProperty property, String path) {
        SchemaProperty items = property.items();
        if (items == null) {
            failOrWarn(path, "array logicalType requires items", DataTypes.StringType);
            return DataTypes.createArrayType(DataTypes.StringType, true);
        }
        DataType elementType = resolveDataType(items, path + ".items");
        boolean containsNull = !Boolean.TRUE.equals(items.required());
        return DataTypes.createArrayType(elementType, containsNull);
    }

    private String resolveFieldName(SchemaProperty property, String path) {
        if (options.fieldNameSource() == SparkSchemaOptions.FieldNameSource.LOGICAL_ONLY) {
            if (property.name() == null || property.name().isBlank()) {
                throw new TypeMappingException(path, "property name is required");
            }
            return property.name();
        }
        if (property.physicalName() != null && !property.physicalName().isBlank()) {
            return property.physicalName();
        }
        if (property.name() == null || property.name().isBlank()) {
            throw new TypeMappingException(path, "property name is required");
        }
        return property.name();
    }

    private static String propertyPath(String parent, SchemaProperty property) {
        String name = property == null ? "?"
                : (property.name() != null ? property.name()
                : (property.physicalName() != null ? property.physicalName() : "?"));
        if (parent == null || parent.isBlank()) {
            return name;
        }
        return parent + "." + name;
    }

    private void failOrWarn(String path, String message, DataType fallback) {
        if (options.isStrict()) {
            throw new TypeMappingException(path, message);
        }
        report.warn(path, message + "; falling back to " + fallback.simpleString());
    }

    private void failOrWarn(String path, String message, StructType fallback) {
        if (options.isStrict()) {
            throw new TypeMappingException(path, message);
        }
        report.warn(path, message + "; falling back to empty struct");
    }
}

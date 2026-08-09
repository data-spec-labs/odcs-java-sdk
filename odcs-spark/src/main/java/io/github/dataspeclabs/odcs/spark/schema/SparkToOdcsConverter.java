package io.github.dataspeclabs.odcs.spark.schema;

import io.github.dataspeclabs.odcs.core.model.v3.LogicalType;
import io.github.dataspeclabs.odcs.core.model.v3.LogicalTypeOptions;
import io.github.dataspeclabs.odcs.core.model.v3.SchemaObject;
import io.github.dataspeclabs.odcs.core.model.v3.SchemaProperty;
import io.github.dataspeclabs.odcs.spark.ConversionReport;
import io.github.dataspeclabs.odcs.spark.SparkSchemaOptions;
import io.github.dataspeclabs.odcs.spark.TypeMappingException;
import org.apache.spark.sql.types.ArrayType;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.DecimalType;
import org.apache.spark.sql.types.MapType;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Converts Spark {@link StructType} / {@link DataType} to ODCS schema definitions.
 */
public final class SparkToOdcsConverter {

    private final SparkSchemaOptions options;
    private final ConversionReport.Builder report;

    public SparkToOdcsConverter(SparkSchemaOptions options, ConversionReport.Builder report) {
        this.options = options;
        this.report = report;
    }

    public SchemaObject toSchemaObject(StructType structType, String name) {
        if (structType == null) {
            throw new TypeMappingException("", "StructType must not be null");
        }
        if (name == null || name.isBlank()) {
            throw new TypeMappingException("", "schema object name is required");
        }
        List<SchemaProperty> properties = new ArrayList<>();
        for (StructField field : structType.fields()) {
            properties.add(toSchemaProperty(field));
        }
        return new SchemaObject(
                null,
                name,
                null,
                "table",
                LogicalType.OBJECT,
                null,
                null,
                null,
                properties,
                null,
                null,
                null,
                null,
                null
        );
    }

    public SchemaProperty toSchemaProperty(StructField field) {
        if (field == null) {
            throw new TypeMappingException("", "StructField must not be null");
        }
        Metadata metadata = field.metadata();
        if (options.readMetadata() && OdcsMetadata.hasOdcsKeys(metadata)) {
            return restoreFromMetadata(field);
        }
        try {
            return inferProperty(field);
        } catch (TypeMappingException ex) {
            if (options.isStrict()) {
                throw ex;
            }
            report.warn(ex.path(), ex.detail() + "; falling back to string");
            Boolean required = field.nullable() ? null : Boolean.TRUE;
            return property(field.name(), null, "string", LogicalType.STRING, null, required,
                    OdcsMetadata.getString(field.metadata(), OdcsMetadata.COMMENT).orElse(null),
                    null, null, null);
        }
    }

    public SchemaProperty toSchemaProperty(String name, DataType dataType, boolean nullable) {
        StructField field = new StructField(name, dataType, nullable, Metadata.empty());
        return toSchemaProperty(field);
    }

    /**
     * Infer ODCS property from Spark type alone (no metadata). Package-visible for {@link MapTypeCodec}.
     */
    static SchemaProperty inferProperty(StructField field) {
        return inferProperty(field.name(), field.dataType(), field.nullable(),
                OdcsMetadata.getString(field.metadata(), OdcsMetadata.COMMENT).orElse(null));
    }

    private static SchemaProperty inferProperty(
            String name,
            DataType dataType,
            boolean nullable,
            String description
    ) {
        Boolean required = nullable ? null : Boolean.TRUE;

        if (dataType instanceof MapType mapType) {
            return MapTypeCodec.fromMapType(name, mapType, nullable, description);
        }
        if (dataType instanceof ArrayType arrayType) {
            SchemaProperty items = inferProperty(
                    "items",
                    arrayType.elementType(),
                    arrayType.containsNull(),
                    null);
            // items.required=true means containsNull=false
            if (!arrayType.containsNull()) {
                items = copyWithRequired(items, true);
            } else {
                items = copyWithRequired(items, null);
            }
            return property(name, null, "array", LogicalType.ARRAY, null, required, description, items, null, null);
        }
        if (dataType instanceof StructType structType) {
            List<SchemaProperty> nested = new ArrayList<>();
            for (StructField f : structType.fields()) {
                nested.add(inferProperty(f));
            }
            return property(name, null, "struct", LogicalType.OBJECT, null, required, description, null, nested, null);
        }
        if (DataTypes.TimestampNTZType.sameType(dataType)) {
            LogicalTypeOptions opts = new LogicalTypeOptions(
                    null, null, null, null, null, null, null, null, null,
                    "false", null, null, null, null, null, null, null);
            return property(name, null, "timestamp_ntz", LogicalType.TIMESTAMP, opts, required, description, null, null, null);
        }
        if (DataTypes.TimestampType.sameType(dataType)) {
            return property(name, null, "timestamp", LogicalType.TIMESTAMP, null, required, description, null, null, null);
        }
        if (DataTypes.DateType.sameType(dataType)) {
            return property(name, null, "date", LogicalType.DATE, null, required, description, null, null, null);
        }
        if (DataTypes.BooleanType.sameType(dataType)) {
            return property(name, null, "boolean", LogicalType.BOOLEAN, null, required, description, null, null, null);
        }
        if (DataTypes.BinaryType.sameType(dataType)) {
            LogicalTypeOptions opts = new LogicalTypeOptions(
                    "binary", null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null);
            return property(name, null, "binary", LogicalType.STRING, opts, required, description, null, null, null);
        }
        if (dataType instanceof DecimalType dt) {
            String physical = "decimal(" + dt.precision() + "," + dt.scale() + ")";
            return property(name, null, physical, LogicalType.NUMBER, null, required, description, null, null, null);
        }
        if (DataTypes.FloatType.sameType(dataType)) {
            LogicalTypeOptions opts = optionsWithFormat("f32");
            return property(name, null, "float", LogicalType.NUMBER, opts, required, description, null, null, null);
        }
        if (DataTypes.DoubleType.sameType(dataType)) {
            LogicalTypeOptions opts = optionsWithFormat("f64");
            return property(name, null, "double", LogicalType.NUMBER, opts, required, description, null, null, null);
        }
        if (DataTypes.ByteType.sameType(dataType)) {
            return property(name, null, "tinyint", LogicalType.INTEGER, optionsWithFormat("i8"), required, description, null, null, null);
        }
        if (DataTypes.ShortType.sameType(dataType)) {
            return property(name, null, "smallint", LogicalType.INTEGER, optionsWithFormat("i16"), required, description, null, null, null);
        }
        if (DataTypes.IntegerType.sameType(dataType)) {
            return property(name, null, "int", LogicalType.INTEGER, optionsWithFormat("i32"), required, description, null, null, null);
        }
        if (DataTypes.LongType.sameType(dataType)) {
            return property(name, null, "bigint", LogicalType.INTEGER, optionsWithFormat("i64"), required, description, null, null, null);
        }
        if (DataTypes.StringType.sameType(dataType)) {
            return property(name, null, "string", LogicalType.STRING, null, required, description, null, null, null);
        }

        // Unsupported — callers using instance methods handle STRICT/LENIENT; static infer throws.
        throw new TypeMappingException(name, "unsupported Spark type: " + dataType.simpleString());
    }

    private SchemaProperty restoreFromMetadata(StructField field) {
        Metadata md = field.metadata();
        String logicalName = OdcsMetadata.getString(md, OdcsMetadata.NAME).orElse(field.name());
        String physicalName = OdcsMetadata.getString(md, OdcsMetadata.NAME).isPresent() ? field.name() : null;
        // When odcs.name is present, Spark field was physicalName; logical name is odcs.name
        if (OdcsMetadata.getString(md, OdcsMetadata.NAME).isPresent()) {
            physicalName = field.name();
        } else {
            physicalName = null;
        }

        Optional<LogicalType> metaLogical = OdcsMetadata.getLogicalType(md);
        String physicalType = OdcsMetadata.getString(md, OdcsMetadata.PHYSICAL_TYPE).orElse(null);
        String description = OdcsMetadata.getString(md, OdcsMetadata.COMMENT).orElse(null);
        Boolean primaryKey = OdcsMetadata.getBoolean(md, OdcsMetadata.PRIMARY_KEY).orElse(null);
        Integer primaryKeyPosition = OdcsMetadata.getInt(md, OdcsMetadata.PRIMARY_KEY_POSITION).orElse(null);
        String classification = OdcsMetadata.getString(md, OdcsMetadata.CLASSIFICATION).orElse(null);
        Boolean critical = OdcsMetadata.getBoolean(md, OdcsMetadata.CRITICAL_DATA_ELEMENT).orElse(null);
        List<String> tags = OdcsMetadata.getTags(md);
        if (tags.isEmpty()) {
            tags = null;
        }

        Boolean required = field.nullable() ? null : Boolean.TRUE;

        DataType dataType = field.dataType();

        // Map via metadata / type
        if (dataType instanceof MapType mapType
                || (physicalType != null && PhysicalTypeParser.parseMap(physicalType).isPresent())
                || OdcsMetadata.getString(md, OdcsMetadata.MAP_KEY_TYPE).isPresent()) {
            SchemaProperty mapProp = MapTypeCodec.fromMapType(
                    logicalName,
                    dataType instanceof MapType mt ? mt : DataTypes.createMapType(DataTypes.StringType, DataTypes.StringType),
                    field.nullable(),
                    description);
            // Overlay metadata-sourced names / governance
            return overlayGovernance(mapProp, physicalName, physicalType, primaryKey, primaryKeyPosition,
                    classification, critical, tags, md);
        }

        if (dataType instanceof ArrayType arrayType) {
            SchemaProperty items = toSchemaProperty(new StructField(
                    "items",
                    arrayType.elementType(),
                    arrayType.containsNull(),
                    Metadata.empty()));
            if (!arrayType.containsNull()) {
                items = copyWithRequired(items, true);
            }
            LogicalType lt = metaLogical.orElse(LogicalType.ARRAY);
            return new SchemaProperty(
                    null, logicalName, physicalName,
                    physicalType != null ? physicalType : "array",
                    description, null, lt, null,
                    primaryKey, primaryKeyPosition, required, null, null, null,
                    classification, null, null, null, null, null, critical,
                    items, null, null, null, null, tags, null);
        }

        if (dataType instanceof StructType structType) {
            List<SchemaProperty> nested = new ArrayList<>();
            for (StructField f : structType.fields()) {
                nested.add(toSchemaProperty(f));
            }
            LogicalType lt = metaLogical.orElse(LogicalType.OBJECT);
            return new SchemaProperty(
                    null, logicalName, physicalName,
                    physicalType != null ? physicalType : "struct",
                    description, null, lt, null,
                    primaryKey, primaryKeyPosition, required, null, null, null,
                    classification, null, null, null, null, null, critical,
                    null, nested, null, null, null, tags, null);
        }

        // Scalars: prefer metadata logical/physical, reconstruct options for formats / ntz
        SchemaProperty inferred = inferProperty(field.name(), dataType, field.nullable(), description);
        LogicalType lt = metaLogical.orElse(inferred.logicalType());
        String phys = physicalType != null ? physicalType : inferred.physicalType();
        LogicalTypeOptions opts = inferred.logicalTypeOptions();

        // Preserve varchar maxLength from physical if present in metadata physicalType
        if (phys != null) {
            Optional<Integer> varcharLen = PhysicalTypeParser.parseVarcharLength(phys);
            if (varcharLen.isPresent()) {
                opts = new LogicalTypeOptions(
                        opts == null ? null : opts.format(),
                        null,
                        varcharLen.get(),
                        null, null, null, null, null, null,
                        opts == null ? null : opts.timezone(),
                        null, null, null, null, null, null, null);
            }
        }

        return new SchemaProperty(
                null, logicalName, physicalName, phys, description, null, lt, opts,
                primaryKey, primaryKeyPosition, required, null, null, null,
                classification, null, null, null, null, null, critical,
                null, null, null, null, null, tags, null);
    }

    private SchemaProperty overlayGovernance(
            SchemaProperty base,
            String physicalName,
            String physicalType,
            Boolean primaryKey,
            Integer primaryKeyPosition,
            String classification,
            Boolean critical,
            List<String> tags,
            Metadata md
    ) {
        // Merge map custom props from metadata if present
        List<io.github.dataspeclabs.odcs.core.model.v3.CustomProperty> custom = base.customProperties();
        Optional<String> metaKey = OdcsMetadata.getString(md, OdcsMetadata.MAP_KEY_TYPE);
        Optional<String> metaVal = OdcsMetadata.getString(md, OdcsMetadata.MAP_VALUE_TYPE);
        if (metaKey.isPresent() || metaVal.isPresent()) {
            List<io.github.dataspeclabs.odcs.core.model.v3.CustomProperty> merged = new ArrayList<>();
            merged.add(new io.github.dataspeclabs.odcs.core.model.v3.CustomProperty(
                    null, MapTypeCodec.MAP_KEY_TYPE,
                    metaKey.orElse(OdcsMetadata.customString(base, MapTypeCodec.MAP_KEY_TYPE).orElse("string")),
                    null));
            merged.add(new io.github.dataspeclabs.odcs.core.model.v3.CustomProperty(
                    null, MapTypeCodec.MAP_VALUE_TYPE,
                    metaVal.orElse(OdcsMetadata.customString(base, MapTypeCodec.MAP_VALUE_TYPE).orElse("string")),
                    null));
            OdcsMetadata.getString(md, OdcsMetadata.MAP_VALUE_REQUIRED)
                    .ifPresent(v -> merged.add(new io.github.dataspeclabs.odcs.core.model.v3.CustomProperty(
                            null, MapTypeCodec.MAP_VALUE_REQUIRED, v, null)));
            custom = merged;
        }

        return new SchemaProperty(
                base.id(),
                base.name(),
                physicalName,
                physicalType != null ? physicalType : base.physicalType(),
                base.description(),
                base.businessName(),
                base.logicalType(),
                base.logicalTypeOptions(),
                primaryKey,
                primaryKeyPosition,
                base.required(),
                base.unique(),
                base.partitioned(),
                base.partitionKeyPosition(),
                classification,
                base.encryptedName(),
                base.transformSourceObjects(),
                base.transformLogic(),
                base.transformDescription(),
                base.examples(),
                critical,
                base.items(),
                base.properties(),
                base.relationships(),
                base.authoritativeDefinitions(),
                base.quality(),
                tags,
                custom
        );
    }

    private static LogicalTypeOptions optionsWithFormat(String format) {
        return new LogicalTypeOptions(
                format, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null);
    }

    private static SchemaProperty property(
            String name,
            String physicalName,
            String physicalType,
            LogicalType logicalType,
            LogicalTypeOptions options,
            Boolean required,
            String description,
            SchemaProperty items,
            List<SchemaProperty> properties,
            List<io.github.dataspeclabs.odcs.core.model.v3.CustomProperty> custom
    ) {
        return new SchemaProperty(
                null, name, physicalName, physicalType, description, null,
                logicalType, options, null, null, required, null, null, null,
                null, null, null, null, null, null, null,
                items, properties, null, null, null, null, custom);
    }

    private static SchemaProperty copyWithRequired(SchemaProperty p, Boolean required) {
        return new SchemaProperty(
                p.id(), p.name(), p.physicalName(), p.physicalType(), p.description(), p.businessName(),
                p.logicalType(), p.logicalTypeOptions(), p.primaryKey(), p.primaryKeyPosition(),
                required, p.unique(), p.partitioned(), p.partitionKeyPosition(), p.classification(),
                p.encryptedName(), p.transformSourceObjects(), p.transformLogic(), p.transformDescription(),
                p.examples(), p.criticalDataElement(), p.items(), p.properties(), p.relationships(),
                p.authoritativeDefinitions(), p.quality(), p.tags(), p.customProperties());
    }
}

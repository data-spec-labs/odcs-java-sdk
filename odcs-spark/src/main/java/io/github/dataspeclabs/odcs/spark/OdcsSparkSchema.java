package io.github.dataspeclabs.odcs.spark;

import io.github.dataspeclabs.odcs.core.model.v3.DataContract;
import io.github.dataspeclabs.odcs.core.model.v3.SchemaObject;
import io.github.dataspeclabs.odcs.core.model.v3.SchemaProperty;
import io.github.dataspeclabs.odcs.spark.schema.OdcsToSparkConverter;
import io.github.dataspeclabs.odcs.spark.schema.SparkToOdcsConverter;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Objects;

/**
 * Public facade for bi-directional conversion between ODCS v3 schema definitions
 * and Spark SQL {@link StructType}.
 *
 * <p>Static methods use {@link SparkSchemaOptions#defaults()}. Prefer
 * {@link #using(SparkSchemaOptions)} when customizing naming, strictness, or metadata.
 */
public final class OdcsSparkSchema {

    private final SparkSchemaOptions options;

    private OdcsSparkSchema(SparkSchemaOptions options) {
        this.options = Objects.requireNonNull(options, "options");
    }

    public static OdcsSparkSchema using(SparkSchemaOptions options) {
        return new OdcsSparkSchema(options);
    }

    // ---- Static convenience (defaults) ----

    public static StructType toStructType(SchemaObject object) {
        return using(SparkSchemaOptions.defaults()).convert(object);
    }

    public static StructType toStructType(DataContract contract, String objectName) {
        return using(SparkSchemaOptions.defaults()).convert(contract, objectName);
    }

    public static SchemaObject toSchemaObject(StructType structType, String name) {
        return using(SparkSchemaOptions.defaults()).convert(structType, name);
    }

    public static StructField toStructField(SchemaProperty property) {
        return using(SparkSchemaOptions.defaults()).convert(property);
    }

    public static DataType toDataType(SchemaProperty property) {
        return using(SparkSchemaOptions.defaults()).convertDataType(property);
    }

    public static SchemaProperty toSchemaProperty(StructField field) {
        return using(SparkSchemaOptions.defaults()).convert(field);
    }

    public static SchemaProperty toSchemaProperty(String name, DataType dataType, boolean nullable) {
        return using(SparkSchemaOptions.defaults()).convert(name, dataType, nullable);
    }

    // ---- Instance API ----

    public StructType convert(SchemaObject object) {
        return convertWithReport(object).value();
    }

    public StructType convert(DataContract contract, String objectName) {
        return convertWithReport(contract, objectName).value();
    }

    public StructField convert(SchemaProperty property) {
        ConversionReport.Builder report = ConversionReport.builder();
        return new OdcsToSparkConverter(options, report).toStructField(property);
    }

    public DataType convertDataType(SchemaProperty property) {
        ConversionReport.Builder report = ConversionReport.builder();
        return new OdcsToSparkConverter(options, report).toDataType(property);
    }

    public SchemaObject convert(StructType structType, String name) {
        return convertWithReport(structType, name).value();
    }

    public SchemaProperty convert(StructField field) {
        ConversionReport.Builder report = ConversionReport.builder();
        return new SparkToOdcsConverter(options, report).toSchemaProperty(field);
    }

    public SchemaProperty convert(String name, DataType dataType, boolean nullable) {
        ConversionReport.Builder report = ConversionReport.builder();
        return new SparkToOdcsConverter(options, report).toSchemaProperty(name, dataType, nullable);
    }

    public ConversionResult<StructType> convertWithReport(SchemaObject object) {
        ConversionReport.Builder report = ConversionReport.builder();
        StructType st = new OdcsToSparkConverter(options, report).toStructType(object);
        return ConversionResult.of(st, report.build());
    }

    public ConversionResult<StructType> convertWithReport(DataContract contract, String objectName) {
        Objects.requireNonNull(contract, "contract");
        Objects.requireNonNull(objectName, "objectName");
        if (contract.schema() == null) {
            throw new TypeMappingException("", "DataContract has no schema objects");
        }
        SchemaObject match = contract.schema().stream()
                .filter(o -> objectName.equals(o.name()) || objectName.equals(o.physicalName()))
                .findFirst()
                .orElseThrow(() -> new TypeMappingException(
                        objectName, "no schema object named '" + objectName + "' in contract"));
        return convertWithReport(match);
    }

    public ConversionResult<SchemaObject> convertWithReport(StructType structType, String name) {
        ConversionReport.Builder report = ConversionReport.builder();
        SchemaObject object = new SparkToOdcsConverter(options, report).toSchemaObject(structType, name);
        return ConversionResult.of(object, report.build());
    }
}

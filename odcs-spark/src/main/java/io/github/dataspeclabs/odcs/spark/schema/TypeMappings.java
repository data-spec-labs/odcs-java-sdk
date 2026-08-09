package io.github.dataspeclabs.odcs.spark.schema;

import io.github.dataspeclabs.odcs.core.model.v3.LogicalType;
import io.github.dataspeclabs.odcs.core.model.v3.LogicalTypeOptions;
import io.github.dataspeclabs.odcs.spark.ConversionReport;
import io.github.dataspeclabs.odcs.spark.SparkSchemaOptions;
import io.github.dataspeclabs.odcs.spark.TypeMappingException;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;

import java.util.Locale;
import java.util.Optional;

/**
 * Canonical ODCS {@link LogicalType} ↔ Spark {@link DataType} defaults and format-driven refinements.
 */
public final class TypeMappings {

    private TypeMappings() {
    }

    public static DataType fromLogicalType(
            LogicalType logicalType,
            LogicalTypeOptions options,
            String physicalType,
            SparkSchemaOptions schemaOptions,
            String path,
            ConversionReport.Builder report
    ) {
        if (logicalType == null) {
            failOrWarn(schemaOptions, report, path, "missing logicalType", DataTypes.StringType);
            return DataTypes.StringType;
        }

        return switch (logicalType) {
            case STRING -> mapString(options, physicalType);
            case BOOLEAN -> DataTypes.BooleanType;
            case INTEGER -> mapInteger(options, physicalType, path, report);
            case NUMBER -> mapNumber(options, physicalType, schemaOptions, path, report);
            case DATE -> DataTypes.DateType;
            case TIMESTAMP -> mapTimestamp(options, physicalType);
            case TIME -> mapTime(schemaOptions);
            case ARRAY, OBJECT -> throw new TypeMappingException(
                    path,
                    "logicalType " + logicalType.value() + " must be handled by nested converters");
        };
    }

    private static DataType mapString(LogicalTypeOptions options, String physicalType) {
        if (physicalType != null) {
            String p = physicalType.trim().toLowerCase(Locale.ROOT);
            if ("binary".equals(p) || "bytes".equals(p) || "bytea".equals(p)) {
                return DataTypes.BinaryType;
            }
        }
        if (options != null && options.format() != null) {
            String format = options.format().trim().toLowerCase(Locale.ROOT);
            if ("binary".equals(format) || "byte".equals(format)) {
                return DataTypes.BinaryType;
            }
        }
        return DataTypes.StringType;
    }

    private static DataType mapInteger(
            LogicalTypeOptions options,
            String physicalType,
            String path,
            ConversionReport.Builder report
    ) {
        Optional<DataType> fromPhysical = PhysicalTypeParser.parseScalarToken(physicalType, true);
        if (fromPhysical.isPresent() && isIntegral(fromPhysical.get())) {
            return fromPhysical.get();
        }

        String format = options == null ? null : options.format();
        if (format == null || format.isBlank()) {
            return DataTypes.IntegerType;
        }
        return switch (format.trim().toLowerCase(Locale.ROOT)) {
            case "i8" -> DataTypes.ByteType;
            case "i16" -> DataTypes.ShortType;
            case "i32" -> DataTypes.IntegerType;
            case "i64" -> DataTypes.LongType;
            case "u8" -> {
                report.warn(path, "unsigned format u8 widened to ShortType");
                yield DataTypes.ShortType;
            }
            case "u16" -> {
                report.warn(path, "unsigned format u16 widened to IntegerType");
                yield DataTypes.IntegerType;
            }
            case "u32" -> {
                report.warn(path, "unsigned format u32 widened to LongType");
                yield DataTypes.LongType;
            }
            case "u64" -> {
                report.warn(path, "unsigned format u64 mapped to DecimalType(20,0)");
                yield DataTypes.createDecimalType(20, 0);
            }
            case "i128", "u128" -> {
                report.warn(path, "format " + format + " mapped to DecimalType(38,0)");
                yield DataTypes.createDecimalType(38, 0);
            }
            default -> DataTypes.IntegerType;
        };
    }

    private static DataType mapNumber(
            LogicalTypeOptions options,
            String physicalType,
            SparkSchemaOptions schemaOptions,
            String path,
            ConversionReport.Builder report
    ) {
        Optional<PhysicalTypeParser.DecimalSpec> decimal = PhysicalTypeParser.parseDecimal(physicalType);
        if (decimal.isPresent()) {
            PhysicalTypeParser.DecimalSpec d = decimal.get();
            if (d.isBare()) {
                if (schemaOptions.requireDecimalPrecision()) {
                    failOrWarn(schemaOptions, report, path,
                            "decimal physicalType requires precision and scale, e.g. decimal(18,2)",
                            DataTypes.createDecimalType(10, 0));
                    return DataTypes.createDecimalType(10, 0);
                }
                return DataTypes.createDecimalType(10, 0);
            }
            return DataTypes.createDecimalType(d.precision(), d.scale());
        }

        Optional<DataType> fromPhysical = PhysicalTypeParser.parseScalarToken(physicalType, true);
        if (fromPhysical.isPresent() && isFloating(fromPhysical.get())) {
            return fromPhysical.get();
        }

        String format = options == null ? null : options.format();
        if (format != null) {
            return switch (format.trim().toLowerCase(Locale.ROOT)) {
                case "f32" -> DataTypes.FloatType;
                case "f64" -> DataTypes.DoubleType;
                default -> DataTypes.DoubleType;
            };
        }
        return DataTypes.DoubleType;
    }

    static DataType mapTimestamp(LogicalTypeOptions options, String physicalType) {
        if (physicalType != null) {
            String p = physicalType.trim().toLowerCase(Locale.ROOT);
            if ("timestamp_ntz".equals(p) || "timestampntz".equals(p)) {
                return DataTypes.TimestampNTZType;
            }
            if ("timestamp".equals(p) || "datetime".equals(p)) {
                return DataTypes.TimestampType;
            }
        }
        if (isTimezoneAware(options)) {
            return DataTypes.TimestampType;
        }
        if (isTimezoneNtz(options)) {
            return DataTypes.TimestampNTZType;
        }
        return DataTypes.TimestampType;
    }

    private static DataType mapTime(SparkSchemaOptions schemaOptions) {
        return switch (schemaOptions.timeTypeMapping()) {
            case STRING -> DataTypes.StringType;
            case LONG_MICROS -> DataTypes.LongType;
        };
    }

    /**
     * Lenient parse of {@code logicalTypeOptions.timezone}:
     * {@code "true"}/{@code "false"} decide awareness; any other non-blank value is a zone id (aware).
     */
    public static boolean isTimezoneAware(LogicalTypeOptions options) {
        if (options == null || options.timezone() == null || options.timezone().isBlank()) {
            return false;
        }
        String tz = options.timezone().trim();
        if ("false".equalsIgnoreCase(tz)) {
            return false;
        }
        return "true".equalsIgnoreCase(tz) || !tz.isBlank();
    }

    public static boolean isTimezoneNtz(LogicalTypeOptions options) {
        if (options == null || options.timezone() == null) {
            return false;
        }
        return "false".equalsIgnoreCase(options.timezone().trim());
    }

    public static String integerFormatFor(DataType dataType) {
        if (DataTypes.ByteType.sameType(dataType)) {
            return "i8";
        }
        if (DataTypes.ShortType.sameType(dataType)) {
            return "i16";
        }
        if (DataTypes.IntegerType.sameType(dataType)) {
            return "i32";
        }
        if (DataTypes.LongType.sameType(dataType)) {
            return "i64";
        }
        return null;
    }

    public static String numberFormatFor(DataType dataType) {
        if (DataTypes.FloatType.sameType(dataType)) {
            return "f32";
        }
        if (DataTypes.DoubleType.sameType(dataType)) {
            return "f64";
        }
        return null;
    }

    private static boolean isIntegral(DataType dt) {
        return DataTypes.ByteType.sameType(dt)
                || DataTypes.ShortType.sameType(dt)
                || DataTypes.IntegerType.sameType(dt)
                || DataTypes.LongType.sameType(dt);
    }

    private static boolean isFloating(DataType dt) {
        return DataTypes.FloatType.sameType(dt) || DataTypes.DoubleType.sameType(dt);
    }

    private static void failOrWarn(
            SparkSchemaOptions options,
            ConversionReport.Builder report,
            String path,
            String message,
            DataType fallback
    ) {
        if (options.isStrict()) {
            throw new TypeMappingException(path, message);
        }
        report.warn(path, message + "; falling back to " + fallback.simpleString());
    }
}

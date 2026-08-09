package io.github.dataspeclabs.odcs.spark.schema;

import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.DecimalType;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses free-form ODCS {@code physicalType} strings into Spark {@link DataType}s
 * or structured tokens (decimal, map&lt;k,v&gt;, varchar).
 */
public final class PhysicalTypeParser {

    private static final Pattern DECIMAL =
            Pattern.compile("(?i)^decimal\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)$");
    private static final Pattern DECIMAL_BARE =
            Pattern.compile("(?i)^decimal$");
    private static final Pattern VARCHAR =
            Pattern.compile("(?i)^(?:var)?char\\s*\\(\\s*(\\d+)\\s*\\)$");
    private static final Pattern MAP =
            Pattern.compile("(?i)^map\\s*<\\s*(.+?)\\s*,\\s*(.+?)\\s*>$");

    private PhysicalTypeParser() {
    }

    public static Optional<DecimalSpec> parseDecimal(String physicalType) {
        if (physicalType == null || physicalType.isBlank()) {
            return Optional.empty();
        }
        String trimmed = physicalType.trim();
        Matcher m = DECIMAL.matcher(trimmed);
        if (m.matches()) {
            return Optional.of(new DecimalSpec(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))));
        }
        if (DECIMAL_BARE.matcher(trimmed).matches()) {
            return Optional.of(DecimalSpec.unspecified());
        }
        return Optional.empty();
    }

    public static Optional<Integer> parseVarcharLength(String physicalType) {
        if (physicalType == null || physicalType.isBlank()) {
            return Optional.empty();
        }
        Matcher m = VARCHAR.matcher(physicalType.trim());
        if (m.matches()) {
            return Optional.of(Integer.parseInt(m.group(1)));
        }
        return Optional.empty();
    }

    public static Optional<MapSpec> parseMap(String physicalType) {
        if (physicalType == null || physicalType.isBlank()) {
            return Optional.empty();
        }
        String trimmed = physicalType.trim();
        if ("map".equalsIgnoreCase(trimmed)) {
            return Optional.of(MapSpec.unspecified());
        }
        Matcher m = MAP.matcher(trimmed);
        if (m.matches()) {
            return Optional.of(new MapSpec(m.group(1).trim(), m.group(2).trim()));
        }
        return Optional.empty();
    }

    public static boolean isExactMap(String physicalType) {
        return physicalType != null && "map".equalsIgnoreCase(physicalType.trim());
    }

    /**
     * Resolve a scalar Spark physical type token (no nesting).
     */
    public static Optional<DataType> parseScalarToken(String token, boolean requireDecimalPrecision) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        String t = token.trim().toLowerCase(Locale.ROOT);

        Optional<DecimalSpec> decimal = parseDecimal(token);
        if (decimal.isPresent()) {
            DecimalSpec d = decimal.get();
            if (d.isBare()) {
                if (requireDecimalPrecision) {
                    return Optional.empty();
                }
                return Optional.of(DataTypes.createDecimalType(10, 0));
            }
            return Optional.of(DataTypes.createDecimalType(d.precision(), d.scale()));
        }

        return switch (t) {
            case "string", "str", "text", "varchar", "char" -> Optional.of(DataTypes.StringType);
            case "boolean", "bool" -> Optional.of(DataTypes.BooleanType);
            case "byte", "tinyint", "int8" -> Optional.of(DataTypes.ByteType);
            case "short", "smallint", "int16" -> Optional.of(DataTypes.ShortType);
            case "int", "integer", "int32" -> Optional.of(DataTypes.IntegerType);
            case "long", "bigint", "int64" -> Optional.of(DataTypes.LongType);
            case "float", "real", "float32" -> Optional.of(DataTypes.FloatType);
            case "double", "float64" -> Optional.of(DataTypes.DoubleType);
            case "date" -> Optional.of(DataTypes.DateType);
            case "timestamp", "datetime" -> Optional.of(DataTypes.TimestampType);
            case "timestamp_ntz", "timestampntz" -> Optional.of(DataTypes.TimestampNTZType);
            case "binary", "bytes", "bytea" -> Optional.of(DataTypes.BinaryType);
            case "time" -> Optional.of(DataTypes.StringType);
            default -> Optional.empty();
        };
    }

    public static String sparkPhysicalToken(DataType dataType) {
        if (dataType instanceof DecimalType dt) {
            return "decimal(" + dt.precision() + "," + dt.scale() + ")";
        }
        return dataType.simpleString().toLowerCase(Locale.ROOT);
    }

    public record DecimalSpec(int precision, int scale, boolean isBare) {
        public DecimalSpec(int precision, int scale) {
            this(precision, scale, false);
        }

        public static DecimalSpec unspecified() {
            return new DecimalSpec(-1, -1, true);
        }
    }

    public record MapSpec(String keyType, String valueType, boolean isBare) {
        public MapSpec(String keyType, String valueType) {
            this(keyType, valueType, false);
        }

        public static MapSpec unspecified() {
            return new MapSpec(null, null, true);
        }
    }
}

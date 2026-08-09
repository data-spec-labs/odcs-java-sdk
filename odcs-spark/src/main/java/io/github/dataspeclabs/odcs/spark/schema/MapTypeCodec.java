package io.github.dataspeclabs.odcs.spark.schema;

import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;
import io.github.dataspeclabs.odcs.core.model.v3.LogicalType;
import io.github.dataspeclabs.odcs.core.model.v3.SchemaProperty;
import io.github.dataspeclabs.odcs.spark.ConversionReport;
import io.github.dataspeclabs.odcs.spark.SparkSchemaOptions;
import io.github.dataspeclabs.odcs.spark.TypeMappingException;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.MapType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A+C hybrid map codec: {@code physicalType: map} + {@code mapKeyType}/{@code mapValueType}
 * custom properties, with {@code map&lt;k,v&gt;} as a secondary parse path.
 * Isolated here so ODCS v3.2 {@code logicalType: map} is a one-class change.
 */
public final class MapTypeCodec {

    public static final String MAP_KEY_TYPE = "mapKeyType";
    public static final String MAP_VALUE_TYPE = "mapValueType";
    public static final String MAP_VALUE_REQUIRED = "mapValueRequired";

    private MapTypeCodec() {
    }

    public static boolean isMapProperty(SchemaProperty property) {
        if (property == null) {
            return false;
        }
        if (PhysicalTypeParser.parseMap(property.physicalType()).isPresent()) {
            return true;
        }
        return OdcsMetadata.customString(property, MAP_KEY_TYPE).isPresent()
                || OdcsMetadata.customString(property, MAP_VALUE_TYPE).isPresent();
    }

    public static DataType toMapType(
            SchemaProperty property,
            SparkSchemaOptions options,
            String path,
            ConversionReport.Builder report,
            BiFunction<List<SchemaProperty>, String, StructType> nestedStructConverter
    ) {
        Optional<String> keyToken = OdcsMetadata.customString(property, MAP_KEY_TYPE);
        Optional<String> valueToken = OdcsMetadata.customString(property, MAP_VALUE_TYPE);
        Optional<PhysicalTypeParser.MapSpec> mapSpec = PhysicalTypeParser.parseMap(property.physicalType());

        if (keyToken.isEmpty() && mapSpec.isPresent() && !mapSpec.get().isBare()) {
            keyToken = Optional.ofNullable(mapSpec.get().keyType());
        }
        if (valueToken.isEmpty() && mapSpec.isPresent() && !mapSpec.get().isBare()) {
            valueToken = Optional.ofNullable(mapSpec.get().valueType());
        }

        if (keyToken.isEmpty()) {
            throwOrWarn(options, report, path, "map property missing mapKeyType", DataTypes.StringType);
            keyToken = Optional.of("string");
        }
        if (valueToken.isEmpty()) {
            throwOrWarn(options, report, path, "map property missing mapValueType", DataTypes.StringType);
            valueToken = Optional.of("string");
        }

        DataType keyType = resolveToken(
                keyToken.get(), property, options, path + ".key", report, nestedStructConverter, false);
        DataType valueType = resolveToken(
                valueToken.get(), property, options, path + ".value", report, nestedStructConverter, true);

        boolean valueContainsNull = true;
        Optional<String> required = OdcsMetadata.customString(property, MAP_VALUE_REQUIRED);
        if (required.isPresent() && "true".equalsIgnoreCase(required.get())) {
            valueContainsNull = false;
        }

        return DataTypes.createMapType(keyType, valueType, valueContainsNull);
    }

    private static DataType resolveToken(
            String token,
            SchemaProperty property,
            SparkSchemaOptions options,
            String path,
            ConversionReport.Builder report,
            BiFunction<List<SchemaProperty>, String, StructType> nestedStructConverter,
            boolean allowObject
    ) {
        String t = token.trim().toLowerCase(Locale.ROOT);
        if (allowObject && ("object".equals(t) || "struct".equals(t))) {
            List<SchemaProperty> props = property.properties();
            if (props == null || props.isEmpty()) {
                throwOrWarn(options, report, path,
                        "map value type object requires nested properties", DataTypes.StringType);
                return DataTypes.StringType;
            }
            return nestedStructConverter.apply(props, path);
        }
        return PhysicalTypeParser.parseScalarToken(token, options.requireDecimalPrecision())
                .orElseGet(() -> {
                    throwOrWarn(options, report, path, "unsupported map type token: " + token, DataTypes.StringType);
                    return DataTypes.StringType;
                });
    }

    /**
     * Import Spark {@link MapType} into the round-trippable ODCS convention.
     */
    public static SchemaProperty fromMapType(
            String name,
            MapType mapType,
            boolean nullable,
            String description
    ) {
        String keyPhysical = PhysicalTypeParser.sparkPhysicalToken(mapType.keyType());
        String valuePhysical;
        List<SchemaProperty> nestedProps = null;

        if (mapType.valueType() instanceof StructType st) {
            valuePhysical = "object";
            nestedProps = new ArrayList<>();
            for (StructField field : st.fields()) {
                nestedProps.add(SparkToOdcsConverter.inferProperty(field));
            }
        } else {
            valuePhysical = PhysicalTypeParser.sparkPhysicalToken(mapType.valueType());
        }

        List<CustomProperty> custom = new ArrayList<>();
        custom.add(new CustomProperty(null, MAP_KEY_TYPE, keyPhysical, null));
        custom.add(new CustomProperty(null, MAP_VALUE_TYPE, valuePhysical, null));
        if (!mapType.valueContainsNull()) {
            custom.add(new CustomProperty(null, MAP_VALUE_REQUIRED, "true", null));
        }

        Boolean required = nullable ? null : Boolean.TRUE;

        return new SchemaProperty(
                null,
                name,
                null,
                "map",
                description,
                null,
                LogicalType.OBJECT,
                null,
                null,
                null,
                required,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                nestedProps,
                null,
                null,
                null,
                null,
                custom
        );
    }

    private static void throwOrWarn(
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

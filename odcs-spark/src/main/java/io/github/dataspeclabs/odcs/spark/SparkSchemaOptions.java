package io.github.dataspeclabs.odcs.spark;

import java.util.Objects;

/**
 * Immutable configuration for {@link OdcsSparkSchema} conversions.
 */
public final class SparkSchemaOptions {

    public enum FieldNameSource {
        /** Use {@code physicalName} when set, otherwise {@code name}. */
        PHYSICAL_THEN_LOGICAL,
        /** Always use the ODCS logical {@code name}. */
        LOGICAL_ONLY
    }

    public enum Strictness {
        /** Unresolved or unsupported types throw {@link TypeMappingException}. */
        STRICT,
        /** Unresolved types fall back to {@code StringType} and record a warning. */
        LENIENT
    }

    public enum TimeTypeMapping {
        /** Map ODCS {@code time} to Spark {@code StringType}, preserving {@code physicalType: time}. */
        STRING,
        /** Map ODCS {@code time} to Spark {@code LongType} (microseconds since midnight). */
        LONG_MICROS
    }

    private final FieldNameSource fieldNameSource;
    private final Strictness strictness;
    private final boolean emitMetadata;
    private final boolean readMetadata;
    private final TimeTypeMapping timeTypeMapping;
    private final boolean requireDecimalPrecision;

    private SparkSchemaOptions(Builder builder) {
        this.fieldNameSource = builder.fieldNameSource;
        this.strictness = builder.strictness;
        this.emitMetadata = builder.emitMetadata;
        this.readMetadata = builder.readMetadata;
        this.timeTypeMapping = builder.timeTypeMapping;
        this.requireDecimalPrecision = builder.requireDecimalPrecision;
    }

    public static SparkSchemaOptions defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public FieldNameSource fieldNameSource() {
        return fieldNameSource;
    }

    public Strictness strictness() {
        return strictness;
    }

    public boolean emitMetadata() {
        return emitMetadata;
    }

    public boolean readMetadata() {
        return readMetadata;
    }

    public TimeTypeMapping timeTypeMapping() {
        return timeTypeMapping;
    }

    public boolean requireDecimalPrecision() {
        return requireDecimalPrecision;
    }

    public boolean isStrict() {
        return strictness == Strictness.STRICT;
    }

    public static final class Builder {
        private FieldNameSource fieldNameSource = FieldNameSource.PHYSICAL_THEN_LOGICAL;
        private Strictness strictness = Strictness.STRICT;
        private boolean emitMetadata = true;
        private boolean readMetadata = true;
        private TimeTypeMapping timeTypeMapping = TimeTypeMapping.STRING;
        private boolean requireDecimalPrecision = true;

        public Builder fieldNameSource(FieldNameSource fieldNameSource) {
            this.fieldNameSource = Objects.requireNonNull(fieldNameSource, "fieldNameSource");
            return this;
        }

        public Builder strictness(Strictness strictness) {
            this.strictness = Objects.requireNonNull(strictness, "strictness");
            return this;
        }

        public Builder emitMetadata(boolean emitMetadata) {
            this.emitMetadata = emitMetadata;
            return this;
        }

        public Builder readMetadata(boolean readMetadata) {
            this.readMetadata = readMetadata;
            return this;
        }

        public Builder timeTypeMapping(TimeTypeMapping timeTypeMapping) {
            this.timeTypeMapping = Objects.requireNonNull(timeTypeMapping, "timeTypeMapping");
            return this;
        }

        public Builder requireDecimalPrecision(boolean requireDecimalPrecision) {
            this.requireDecimalPrecision = requireDecimalPrecision;
            return this;
        }

        public SparkSchemaOptions build() {
            return new SparkSchemaOptions(this);
        }
    }
}

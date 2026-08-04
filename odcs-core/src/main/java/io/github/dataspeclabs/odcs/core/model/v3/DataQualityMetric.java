package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Predefined library metrics for data quality rules.
 */
public enum DataQualityMetric {
    NULL_VALUES("nullValues"),
    MISSING_VALUES("missingValues"),
    INVALID_VALUES("invalidValues"),
    DUPLICATE_VALUES("duplicateValues"),
    ROW_COUNT("rowCount");

    private final String value;

    DataQualityMetric(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static DataQualityMetric fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (DataQualityMetric metric : values()) {
            if (metric.value.equalsIgnoreCase(value)) {
                return metric;
            }
        }
        throw new IllegalArgumentException("Unknown data quality metric: " + value);
    }
}

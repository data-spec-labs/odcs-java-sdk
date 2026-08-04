package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Unit for data quality measurements.
 */
public enum DataQualityUnit {
    ROWS("rows"),
    PERCENT("percent");

    private final String value;

    DataQualityUnit(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static DataQualityUnit fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (DataQualityUnit unit : values()) {
            if (unit.value.equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown data quality unit: " + value);
    }
}

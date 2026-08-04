package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Data quality rule type.
 */
public enum DataQualityType {
    TEXT("text"),
    LIBRARY("library"),
    SQL("sql"),
    CUSTOM("custom");

    private final String value;

    DataQualityType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static DataQualityType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (DataQualityType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown data quality type: " + value);
    }
}

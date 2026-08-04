package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Logical data type for schema properties (ODCS v3.x).
 */
public enum LogicalType {
    STRING("string"),
    DATE("date"),
    TIMESTAMP("timestamp"),
    TIME("time"),
    NUMBER("number"),
    INTEGER("integer"),
    OBJECT("object"),
    ARRAY("array"),
    BOOLEAN("boolean");

    private final String value;

    LogicalType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static LogicalType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (LogicalType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown logicalType: " + value);
    }
}

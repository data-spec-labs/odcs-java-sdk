package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Relationship type. Currently only foreignKey is defined by ODCS v3.1.0.
 */
public enum RelationshipType {
    FOREIGN_KEY("foreignKey");

    private final String value;

    RelationshipType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static RelationshipType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (RelationshipType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown relationship type: " + value);
    }
}

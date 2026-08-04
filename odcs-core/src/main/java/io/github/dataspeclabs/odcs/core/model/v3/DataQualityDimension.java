package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Data quality dimension.
 */
public enum DataQualityDimension {
    ACCURACY("accuracy"),
    COMPLETENESS("completeness"),
    CONFORMITY("conformity"),
    CONSISTENCY("consistency"),
    COVERAGE("coverage"),
    TIMELINESS("timeliness"),
    UNIQUENESS("uniqueness");

    private final String value;

    DataQualityDimension(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static DataQualityDimension fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (DataQualityDimension dimension : values()) {
            if (dimension.value.equalsIgnoreCase(value)) {
                return dimension;
            }
        }
        throw new IllegalArgumentException("Unknown data quality dimension: " + value);
    }
}

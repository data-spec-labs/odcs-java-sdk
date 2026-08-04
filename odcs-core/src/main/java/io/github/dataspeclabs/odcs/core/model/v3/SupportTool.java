package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Support communication tool.
 */
public enum SupportTool {
    EMAIL("email"),
    SLACK("slack"),
    TEAMS("teams"),
    DISCORD("discord"),
    TICKET("ticket"),
    GOOGLECHAT("googlechat"),
    OTHER("other");

    private final String value;

    SupportTool(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static SupportTool fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (SupportTool tool : values()) {
            if (tool.value.equalsIgnoreCase(value)) {
                return tool;
            }
        }
        return OTHER;
    }
}

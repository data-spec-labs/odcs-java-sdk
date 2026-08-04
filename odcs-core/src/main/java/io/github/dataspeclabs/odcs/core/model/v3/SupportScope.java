package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Scope of a support channel.
 */
public enum SupportScope {
    INTERACTIVE("interactive"),
    ANNOUNCEMENTS("announcements"),
    ISSUES("issues"),
    NOTIFICATIONS("notifications");

    private final String value;

    SupportScope(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static SupportScope fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (SupportScope scope : values()) {
            if (scope.value.equalsIgnoreCase(value)) {
                return scope;
            }
        }
        throw new IllegalArgumentException("Unknown support scope: " + value);
    }
}

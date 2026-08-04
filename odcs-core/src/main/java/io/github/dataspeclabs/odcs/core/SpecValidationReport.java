package io.github.dataspeclabs.odcs.core;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Result of validating an ODCS document against a Bitol JSON Schema.
 *
 * @param valid           {@code true} when there are no schema errors
 * @param errors          schema violations (empty when valid)
 * @param resolvedVersion schema version key actually used (e.g. {@code v3.1.0})
 * @param versionWarning  optional warning when apiVersion was missing/unknown and a fallback was used
 */
public record SpecValidationReport(
        boolean valid,
        List<SpecValidationError> errors,
        String resolvedVersion,
        String versionWarning
) {
    public SpecValidationReport {
        errors = errors == null ? List.of() : List.copyOf(errors);
        Objects.requireNonNull(resolvedVersion, "resolvedVersion");
    }

    public static SpecValidationReport of(
            List<SpecValidationError> errors,
            String resolvedVersion,
            String versionWarning
    ) {
        List<SpecValidationError> safe = errors == null ? List.of() : errors;
        return new SpecValidationReport(safe.isEmpty(), safe, resolvedVersion, versionWarning);
    }

    public static SpecValidationReport ok(String resolvedVersion) {
        return new SpecValidationReport(true, Collections.emptyList(), resolvedVersion, null);
    }
}

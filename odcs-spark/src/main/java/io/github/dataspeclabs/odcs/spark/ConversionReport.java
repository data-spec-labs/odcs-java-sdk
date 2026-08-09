package io.github.dataspeclabs.odcs.spark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Warnings collected during an ODCS ↔ Spark schema conversion (typically in LENIENT mode).
 */
public final class ConversionReport {

    private final List<String> warnings;

    private ConversionReport(List<String> warnings) {
        this.warnings = Collections.unmodifiableList(new ArrayList<>(warnings));
    }

    public static ConversionReport empty() {
        return new ConversionReport(List.of());
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> warnings() {
        return warnings;
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public static final class Builder {
        private final List<String> warnings = new ArrayList<>();

        public Builder warn(String path, String message) {
            Objects.requireNonNull(message, "message");
            if (path == null || path.isBlank()) {
                warnings.add(message);
            } else {
                warnings.add(path + ": " + message);
            }
            return this;
        }

        public Builder warn(String message) {
            return warn(null, message);
        }

        public ConversionReport build() {
            return new ConversionReport(warnings);
        }
    }
}

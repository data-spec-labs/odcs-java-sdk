package io.github.dataspeclabs.odcs.spark;

import java.util.Objects;

/**
 * Conversion outcome plus any warnings collected under LENIENT mode.
 *
 * @param <T> converted value type
 */
public record ConversionResult<T>(T value, ConversionReport report) {

    public ConversionResult {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(report, "report");
    }

    public static <T> ConversionResult<T> of(T value, ConversionReport report) {
        return new ConversionResult<>(value, report);
    }

    public boolean hasWarnings() {
        return report.hasWarnings();
    }
}

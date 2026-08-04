package io.github.dataspeclabs.odcs.core;

/**
 * A single JSON Schema validation error for an ODCS contract document.
 *
 * @param path    prettified instance path (e.g. {@code schema[0].name}), or {@code (root)}
 * @param message human-readable error message
 * @param keyword JSON Schema keyword that failed (e.g. {@code required}, {@code additionalProperties})
 */
public record SpecValidationError(String path, String message, String keyword) {
}

package io.github.dataspeclabs.odcs.spark;

/**
 * Thrown when an ODCS ↔ Spark type conversion cannot be performed under
 * {@link SparkSchemaOptions.Strictness#STRICT} mode.
 */
public class TypeMappingException extends RuntimeException {

    private final String path;
    private final String detail;

    public TypeMappingException(String path, String message) {
        super(format(path, message));
        this.path = path;
        this.detail = message;
    }

    public TypeMappingException(String path, String message, Throwable cause) {
        super(format(path, message), cause);
        this.path = path;
        this.detail = message;
    }

    public String path() {
        return path;
    }

    /** Message without the path prefix. */
    public String detail() {
        return detail;
    }

    private static String format(String path, String message) {
        if (path == null || path.isBlank()) {
            return message;
        }
        return path + ": " + message;
    }
}

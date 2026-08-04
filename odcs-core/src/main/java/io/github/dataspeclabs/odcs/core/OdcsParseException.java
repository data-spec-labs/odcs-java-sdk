package io.github.dataspeclabs.odcs.core;

/**
 * Thrown when parsing or writing an ODCS contract fails.
 */
public class OdcsParseException extends OdcsException {

    public OdcsParseException(String message) {
        super(message);
    }

    public OdcsParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

package io.github.dataspeclabs.odcs.core;

/**
 * Base unchecked exception for ODCS SDK failures.
 */
public class OdcsException extends RuntimeException {

    public OdcsException(String message) {
        super(message);
    }

    public OdcsException(String message, Throwable cause) {
        super(message, cause);
    }
}

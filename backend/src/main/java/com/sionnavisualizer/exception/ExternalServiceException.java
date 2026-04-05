package com.sionnavisualizer.exception;

/**
 * Thrown when an external service call fails (Python bridge, Claude API, etc.).
 * Caught by GlobalExceptionHandler and returned as HTTP 503.
 */
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

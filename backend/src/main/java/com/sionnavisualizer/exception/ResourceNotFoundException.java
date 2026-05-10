package com.sionnavisualizer.exception;

/**
 * Thrown when a requested resource (simulation, user, API key, etc.) is not found in the database.
 * Caught by GlobalExceptionHandler and returned as HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

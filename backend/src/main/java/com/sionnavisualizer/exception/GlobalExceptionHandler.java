package com.sionnavisualizer.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler — ensures every API error returns a consistent JSON structure.
 *
 * Response format:
 * {
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "...",
 *   "timestamp": "2025-01-15T10:30:00",
 *   "path": "/api/simulate/channel-model"
 * }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── Validation errors from @Valid annotations ─────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        String fieldErrors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String field = ((FieldError) error).getField();
                    String msg = error.getDefaultMessage();
                    return field + ": " + msg;
                })
                .collect(Collectors.joining("; "));

        log.warn("Validation failed: {}", fieldErrors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, fieldErrors, extractPath(request));
    }

    // ── Resource not found (custom exception) ─────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), extractPath(request));
    }

    // ── Illegal argument / bad input ──────────────────────────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadArgument(
            IllegalArgumentException ex, WebRequest request) {
        log.warn("Bad argument: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), extractPath(request));
    }

    // ── Spring ResponseStatusException (used by circuit breaker fallbacks) ────
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            ResponseStatusException ex, WebRequest request) {
        log.error("ResponseStatusException: {} — {}", ex.getStatusCode(), ex.getReason());
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return buildErrorResponse(status, ex.getReason(), extractPath(request));
    }

    // ── Security / authorization errors ───────────────────────────────────────
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            SecurityException ex, WebRequest request) {
        log.warn("Security violation: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), extractPath(request));
    }

    // ── Catch-all for unhandled exceptions ────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        // SECURITY FIX: Log full stack trace for debugging but never expose internals to client
        log.error("Unhandled exception on {}: ", extractPath(request), ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Our team has been notified.",
                extractPath(request));
    }

    // ── Helper: build consistent error response body ──────────────────────────
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("path", path);
        return ResponseEntity.status(status).body(body);
    }

    // ── Helper: extract request path from WebRequest ──────────────────────────
    private String extractPath(WebRequest request) {
        String desc = request.getDescription(false);
        // WebRequest.getDescription(false) returns "uri=/api/..."
        return desc.replace("uri=", "");
    }
}

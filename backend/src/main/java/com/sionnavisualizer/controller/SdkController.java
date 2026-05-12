package com.sionnavisualizer.controller;

import com.sionnavisualizer.dto.SdkTrackRequest;
import com.sionnavisualizer.dto.SdkTrackResponse;
import com.sionnavisualizer.service.SdkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sdk")
public class SdkController {
    private final SdkService sdkService;

    public SdkController(SdkService sdkService) {
        this.sdkService = sdkService;
    }

    /** Receives tracked simulation payloads from Python SDK and returns share URL. */
    @PostMapping("/track")
    public ResponseEntity<?> trackSimulation(
            @RequestHeader(name = "X-API-Key", required = false) String apiKey,
            @RequestBody SdkTrackRequest request
    ) {
        try {
            if (apiKey == null || apiKey.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Missing X-API-Key header"));
            }
            SdkTrackResponse response = sdkService.trackSimulation(request, apiKey);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "SDK tracking failed: " + ex.getMessage()));
        }
    }

    /** Validates SDK API key and returns a simple status object. */
    @GetMapping("/validate")
    public ResponseEntity<?> validateApiKey(
            @RequestHeader(name = "X-API-Key", required = false) String apiKey
    ) {
        if (apiKey == null || apiKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Missing X-API-Key header"));
        }
        boolean valid = sdkService.validateApiKey(apiKey);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Invalid API key"));
        }
        return ResponseEntity.ok(Map.of("valid", true, "message", "API key is valid"));
    }
}

package com.sionnavisualizer.controller;

import com.sionnavisualizer.dto.ApiKeyRequestDto;
import com.sionnavisualizer.model.ApiKey;
import com.sionnavisualizer.service.ApiKeyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/keys")
@CrossOrigin(originPatterns = {"https://*.vercel.app", "http://localhost:4200"})
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateApiKey(@Valid @RequestBody ApiKeyRequestDto request, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String email = authentication.getName();
        ApiKey key = apiKeyService.generateApiKey(email, request.getDescription());
        
        return ResponseEntity.ok(Map.of(
            "apiKey", key.getKeyValue(),
            "createdAt", key.getCreatedAt().toString(),
            "description", key.getDescription()
        ));
    }

    @GetMapping("/my-keys")
    public ResponseEntity<List<ApiKey>> getMyKeys(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(null);
        }
        String email = authentication.getName();
        return ResponseEntity.ok(apiKeyService.getApiKeysByEmail(email));
    }

    @DeleteMapping("/{keyValue}")
    public ResponseEntity<?> deleteApiKey(@PathVariable String keyValue, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String email = authentication.getName();
        try {
            apiKeyService.deactivateApiKey(keyValue, email);
            return ResponseEntity.ok(Map.of("message", "API Key revoked successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}

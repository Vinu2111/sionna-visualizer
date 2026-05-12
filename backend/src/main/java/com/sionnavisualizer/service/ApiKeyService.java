package com.sionnavisualizer.service;

import com.sionnavisualizer.model.ApiKey;
import com.sionnavisualizer.repository.ApiKeyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    public ApiKey generateApiKey(String ownerEmail, String description) {
        ApiKey key = new ApiKey();
        key.setKeyValue("sk-sionna-" + UUID.randomUUID().toString());
        key.setOwnerEmail(ownerEmail);
        key.setDescription(description);
        key.setCreatedAt(LocalDateTime.now());
        key.setIsActive(true);
        key.setRequestCount(0L);
        return apiKeyRepository.save(key);
    }

    public boolean validateApiKey(String keyValue) {
        ApiKey key = apiKeyRepository.findByKeyValue(keyValue).orElse(null);
        if (key != null && key.getIsActive()) {
            key.setLastUsedAt(LocalDateTime.now());
            key.setRequestCount(key.getRequestCount() + 1);
            apiKeyRepository.save(key);
            return true;
        }
        return false;
    }

    public ApiKey getApiKeyStats(String keyValue) {
        return apiKeyRepository.findByKeyValue(keyValue)
                .orElseThrow(() -> new RuntimeException("API Key not found"));
    }

    public List<ApiKey> getApiKeysByEmail(String email) {
        return apiKeyRepository.findByOwnerEmail(email);
    }

    public void deactivateApiKey(String keyValue, String ownerEmail) {
        ApiKey key = apiKeyRepository.findByKeyValue(keyValue)
                .orElseThrow(() -> new RuntimeException("API Key not found"));

        if (!key.getOwnerEmail().equals(ownerEmail)) {
            throw new RuntimeException("Unauthorized");
        }
        key.setIsActive(false);
        apiKeyRepository.save(key);
    }

    // Added for SDK authentication — F15
    public ApiKey getActiveApiKey(String keyValue) {
        return apiKeyRepository.findByKeyValue(keyValue)
            .filter(k -> k.getIsActive())
            .orElse(null);
    }

    // Added for SDK usage tracking — F15
    public void incrementUsage(ApiKey apiKey) {
        apiKey.setRequestCount(apiKey.getRequestCount() + 1);
        apiKeyRepository.save(apiKey);
    }
}

package com.sionnavisualizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sionnavisualizer.dto.BerDataDto;
import com.sionnavisualizer.dto.SdkTrackRequest;
import com.sionnavisualizer.dto.SdkTrackResponse;
import com.sionnavisualizer.model.ApiKey;
import com.sionnavisualizer.model.SdkTrack;
import com.sionnavisualizer.model.SimulationResult;
import com.sionnavisualizer.repository.SdkTrackRepository;
import com.sionnavisualizer.repository.SimulationResultRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SdkService {
    private final ApiKeyService apiKeyService;
    private final SimulationResultRepository simulationResultRepository;
    private final SdkTrackRepository sdkTrackRepository;
    private final ObjectMapper objectMapper;

    @Value("${FRONTEND_URL:https://sionna-visualizer.vercel.app}")
    private String frontendUrl;

    public SdkService(
            ApiKeyService apiKeyService,
            SimulationResultRepository simulationResultRepository,
            SdkTrackRepository sdkTrackRepository,
            ObjectMapper objectMapper
    ) {
        this.apiKeyService = apiKeyService;
        this.simulationResultRepository = simulationResultRepository;
        this.sdkTrackRepository = sdkTrackRepository;
        this.objectMapper = objectMapper;
    }

    /** Validates that an API key exists and is active. */
    public boolean validateApiKey(String apiKey) {
        return apiKey != null && !apiKey.isBlank() && apiKeyService.getActiveApiKey(apiKey) != null;
    }

    /** Checks simple per-key daily rate limit using request counter. */
    public void checkRateLimit(String apiKey) {
        ApiKey key = apiKeyService.getActiveApiKey(apiKey);
        if (key == null) {
            throw new IllegalArgumentException("Invalid API key");
        }
        if (key.getRequestCount() != null && key.getRequestCount() >= 100) {
            throw new IllegalStateException("Rate limit exceeded. 100 requests per day per API key.");
        }
    }

    /** Creates simulation record from SDK payload and returns shareable URL response. */
    public SdkTrackResponse trackSimulation(SdkTrackRequest request, String apiKey) {
        ApiKey key = apiKeyService.getActiveApiKey(apiKey);
        if (key == null) {
            throw new IllegalArgumentException("Invalid API key");
        }
        checkRateLimit(apiKey);

        if (request == null) {
            throw new IllegalArgumentException("Request body is required.");
        }

        SimulationResult simulation = buildSimulationEntity(request);
        simulationResultRepository.save(simulation);

        SdkTrack sdkTrack = new SdkTrack();
        sdkTrack.setApiKeyId(key.getId());
        sdkTrack.setSimulationId(simulation.getId());
        sdkTrack.setSdkVersion(request.getSdkVersion() != null ? request.getSdkVersion() : "1.0.0");
        sdkTrack.setSdkLanguage(request.getSdkLanguage() != null ? request.getSdkLanguage() : "python");
        sdkTrack.setSimulationType(simulation.getSimulationType());
        sdkTrack.setTitle(request.getTitle());
        sdkTrack.setTags(toTagsText(request.getTags()));
        sdkTrack.setTrackedAt(LocalDateTime.now());
        sdkTrackRepository.save(sdkTrack);

        apiKeyService.incrementUsage(key);

        SdkTrackResponse response = new SdkTrackResponse();
        response.setSuccess(true);
        response.setSimulationId(simulation.getId());
        response.setShareableUrl(generateShareableUrl(simulation.getId()));
        response.setMessage("Simulation tracked successfully");
        return response;
    }

    /** Generates public share URL from simulation ID lookup. */
    public String generateShareableUrl(Long simulationId) {
        SimulationResult simulation = simulationResultRepository.findById(simulationId)
                .orElseThrow(() -> new RuntimeException("Simulation not found: " + simulationId));
        String base = frontendUrl != null && !frontendUrl.isBlank()
                ? frontendUrl
                : "https://sionna-visualizer.vercel.app";
        return base + "/share/" + simulation.getShareToken();
    }

    private SimulationResult buildSimulationEntity(SdkTrackRequest request) {
        try {
            SimulationResult simulation = new SimulationResult();
            simulation.setSimulationType(request.getSimulationType() != null ? request.getSimulationType() : "AWGN_BER");
            simulation.setTimestamp(LocalDateTime.now());
            simulation.setCreatedAt(LocalDateTime.now());
            simulation.setIsPublic(true);
            simulation.setShareToken(UUID.randomUUID().toString());
            simulation.setTrackedViaSdk(true);
            simulation.setSdkVersion(request.getSdkVersion() != null ? request.getSdkVersion() : "1.0.0");
            simulation.setSdkLanguage(request.getSdkLanguage() != null ? request.getSdkLanguage() : "python");

            BerDataDto ber = request.getBerData();
            if (ber != null && ber.getSnrRangeDb() != null && ber.getSimulatedBer() != null) {
                simulation.setSnrDb(objectMapper.writeValueAsString(ber.getSnrRangeDb()));
                simulation.setBerSimulated(objectMapper.writeValueAsString(ber.getSimulatedBer()));
                simulation.setBerTheoretical(objectMapper.writeValueAsString(
                        ber.getTheoreticalBer() != null ? ber.getTheoreticalBer() : List.of()
                ));
                simulation.setModulationType(ber.getModulation() != null ? ber.getModulation() : "QPSK");
                simulation.setFrequencyGhz(ber.getFrequencyGhz() != null ? BigDecimal.valueOf(ber.getFrequencyGhz()) : BigDecimal.valueOf(28.0));
                simulation.setNumAntennas(ber.getNumAntennasTx() != null ? ber.getNumAntennasTx() : 1);
                simulation.setHardwareUsed("Tracked via Python SDK");
                if (ber.getSnrRangeDb().size() > 0) {
                    simulation.setSnrMin(BigDecimal.valueOf(ber.getSnrRangeDb().stream().min(Double::compareTo).orElse(-10.0)));
                    simulation.setSnrMax(BigDecimal.valueOf(ber.getSnrRangeDb().stream().max(Double::compareTo).orElse(30.0)));
                }
            } else {
                // For non-BER payloads we preserve original data in a generic JSON field.
                simulation.setSnrDb(objectMapper.writeValueAsString(request.getRawData() != null ? request.getRawData() : request));
                simulation.setHardwareUsed("Tracked via Python SDK");
            }

            return simulation;
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize SDK payload: " + e.getMessage(), e);
        }
    }

    private String toTagsText(List<String> tags) {
        if (tags == null || tags.isEmpty()) return "[]";
        try {
            return objectMapper.writeValueAsString(tags);
        } catch (Exception ex) {
            return "[]";
        }
    }
}

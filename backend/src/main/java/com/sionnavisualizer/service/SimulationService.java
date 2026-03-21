package com.sionnavisualizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sionnavisualizer.dto.SimulationDto;
import com.sionnavisualizer.dto.SimulationRequestDto;
import com.sionnavisualizer.model.SimulationResult;
import com.sionnavisualizer.repository.SimulationResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SimulationService — core business logic for:
 *
 * 1. Posting simulation parameters to the Python bridge (real AWGN computation)
 * 2. Parsing the dual-BER response (theoretical + simulated)
 * 3. Saving the full result to PostgreSQL
 * 4. Returning the parsed DTO to the controller → Angular
 */
@Service
public class SimulationService {

    /** Base URL for the Python FastAPI bridge — injected from application.yml */
    @Value("${python-bridge.url}")
    private String PYTHON_BRIDGE_BASE_URL;

    private final RestTemplate restTemplate;
    private final SimulationResultRepository simulationResultRepository;
    private final ObjectMapper objectMapper;

    public SimulationService(RestTemplate restTemplate,
                              SimulationResultRepository simulationResultRepository,
                              ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.simulationResultRepository = simulationResultRepository;
        this.objectMapper = objectMapper;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Run a simulation with default QPSK rate-1/2 parameters.
     * Used by the GET /api/simulate/demo endpoint for quick dashboard loads.
     */
    public SimulationDto runDemoSimulation() {
        return runSimulation(new SimulationRequestDto());
    }

    /**
     * Run a simulation with the supplied parameters and persist the result.
     *
     * Flow:  Java → POST Python /simulate → parse JSON → save PostgreSQL → return DTO
     *
     * @param requestParams the simulation parameters chosen by the user (or defaults)
     * @return the full SimulationDto with both BER curves
     */
    public SimulationDto runSimulation(SimulationRequestDto requestParams) {
        try {
            // ── Step 1: Build POST request to Python bridge ───────────────────
            String simulateUrl = buildSimulateUrl();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SimulationRequestDto> httpRequest = new HttpEntity<>(requestParams, headers);

            // ── Step 2: POST to Python — deserialize into SimulationDto ───────
            SimulationDto dto = restTemplate.postForObject(simulateUrl, httpRequest, SimulationDto.class);

            if (dto == null) {
                throw new RuntimeException("Python bridge returned an empty response");
            }

            // ── Step 3: Serialize arrays to JSON strings for DB storage ────────
            String snrDbJson = objectMapper.writeValueAsString(dto.getSnr_db());
            String berThJson = objectMapper.writeValueAsString(dto.getBer_theoretical());
            String berSimJson = objectMapper.writeValueAsString(dto.getBer_simulated());

            // ── Step 4: Persist to PostgreSQL ─────────────────────────────────
            SimulationResult entity = new SimulationResult();
            entity.setSnrDb(snrDbJson);
            entity.setBerTheoretical(berThJson);
            entity.setBerSimulated(berSimJson);
            entity.setModulationType(dto.getModulation());
            entity.setCodeRate(dto.getCode_rate() != null
                    ? BigDecimal.valueOf(dto.getCode_rate()) : null);
            entity.setSnrMin(BigDecimal.valueOf(requestParams.getSnr_min()));
            entity.setSnrMax(BigDecimal.valueOf(requestParams.getSnr_max()));
            entity.setSimulationTimeMs(dto.getSimulation_time_ms());
            entity.setHardwareUsed("AWGN CPU (numpy/scipy)");
            entity.setTimestamp(LocalDateTime.now());
            entity.setShareToken(java.util.UUID.randomUUID().toString());
            entity.setIsPublic(true);

            simulationResultRepository.save(entity);

            // ── Step 5: Return DTO to controller ──────────────────────────────
            return dto;

        } catch (RestClientException ex) {
            throw new RuntimeException(
                    "Could not reach the Python bridge at " + PYTHON_BRIDGE_BASE_URL
                    + ". Ensure uvicorn is running. Details: " + ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Simulation failed: " + ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves all previously run simulations from PostgreSQL, newest first.
     */
    public List<SimulationResult> getAllSimulations() {
        return simulationResultRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Fetch a single simulation by its public share token.
     */
    public SimulationDto getSimulationByShareToken(String token) {
        SimulationResult entity = simulationResultRepository.findByShareToken(token)
            .orElseThrow(() -> new RuntimeException("No simulation found for token: " + token));

        if (!entity.getIsPublic()) {
            throw new RuntimeException("This simulation is not publicly shared.");
        }

        SimulationDto dto = new SimulationDto();
        try {
            dto.setSnr_db(objectMapper.readValue(entity.getSnrDb(), List.class));
            dto.setBer_theoretical(objectMapper.readValue(entity.getBerTheoretical(), List.class));
            dto.setBer_simulated(objectMapper.readValue(entity.getBerSimulated(), List.class));
            dto.setModulation(entity.getModulationType());
            dto.setCode_rate(entity.getCodeRate() != null ? entity.getCodeRate().doubleValue() : null);
            dto.setSimulation_time_ms(entity.getSimulationTimeMs());
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize simulation from DB: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Builds the full URL for the Python /simulate endpoint.
     * Strips any trailing /simulate/demo path that may have been in the old config.
     */
    private String buildSimulateUrl() {
        String base = PYTHON_BRIDGE_BASE_URL;
        // Remove old legacy path suffix if present in the config value
        if (base.endsWith("/simulate/demo")) {
            base = base.substring(0, base.length() - "/simulate/demo".length());
        } else if (base.endsWith("/simulate")) {
            base = base.substring(0, base.length() - "/simulate".length());
        }
        return base + "/simulate";
    }
}

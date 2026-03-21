package com.sionnavisualizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sionnavisualizer.dto.SimulationDto;
import com.sionnavisualizer.dto.SimulationRequestDto;
import com.sionnavisualizer.dto.BeamPatternRequestDto;
import com.sionnavisualizer.dto.BeamPatternResultDto;
import com.sionnavisualizer.dto.ModulationComparisonRequestDto;
import com.sionnavisualizer.dto.ModulationComparisonResultDto;
import com.sionnavisualizer.dto.ComparisonResponseDto;
import com.sionnavisualizer.dto.SimulationResultDto;
import com.sionnavisualizer.dto.ChannelCapacityRequestDto;
import com.sionnavisualizer.dto.ChannelCapacityResultDto;
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

    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "pythonBridge", fallbackMethod = "simulationFallback")
    @io.github.resilience4j.timelimiter.annotation.TimeLimiter(name = "pythonBridge")
    public SimulationDto runDemoSimulation() {
        return runSimulation(new SimulationRequestDto());
    }

    public SimulationDto simulationFallback(Throwable t) {
        throw new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
            "Simulation engine is temporarily unavailable. Please try again in 30 seconds.");
    }

    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "pythonBridge", fallbackMethod = "simulationFallback")
    @io.github.resilience4j.timelimiter.annotation.TimeLimiter(name = "pythonBridge")
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

    public SimulationDto simulationFallback(SimulationRequestDto requestParams, Throwable t) {
        throw new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
            "Simulation engine is temporarily unavailable. Please try again in 30 seconds.");
    }

    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "pythonBridge", fallbackMethod = "beamPatternFallback")
    @io.github.resilience4j.timelimiter.annotation.TimeLimiter(name = "pythonBridge")
    public BeamPatternResultDto runBeamPattern(BeamPatternRequestDto requestParams) {
        try {
            String url = buildSimulateUrl() + "/beam-pattern";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<BeamPatternRequestDto> httpRequest = new HttpEntity<>(requestParams, headers);

            BeamPatternResultDto dto = restTemplate.postForObject(url, httpRequest, BeamPatternResultDto.class);

            if (dto == null) {
                throw new RuntimeException("Python bridge returned an empty response for beam pattern");
            }

            String anglesJson = objectMapper.writeValueAsString(dto.getAngles());
            String patternJson = objectMapper.writeValueAsString(dto.getPattern_db());

            SimulationResult entity = new SimulationResult();
            entity.setSimulationType("BEAM_PATTERN");
            entity.setBeamAngles(anglesJson);
            entity.setBeamPatternDb(patternJson);
            entity.setSteeringAngle(BigDecimal.valueOf(dto.getSteering_angle()));
            entity.setNumAntennas(dto.getNum_antennas());
            entity.setFrequencyGhz(BigDecimal.valueOf(dto.getFrequency_ghz()));
            entity.setMainLobeWidth(BigDecimal.valueOf(dto.getMain_lobe_width()));
            entity.setSideLobeLevel(BigDecimal.valueOf(dto.getSide_lobe_level()));
            
            entity.setHardwareUsed("Mathematical ULA (numpy)");
            entity.setTimestamp(LocalDateTime.now());
            entity.setShareToken(java.util.UUID.randomUUID().toString());
            entity.setIsPublic(true);

            simulationResultRepository.save(entity);

            return dto;

        } catch (RestClientException ex) {
            throw new RuntimeException("Could not reach the Python bridge. Details: " + ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Beam pattern generation failed: " + ex.getMessage(), ex);
        }
    }

    public BeamPatternResultDto beamPatternFallback(BeamPatternRequestDto requestParams, Throwable t) {
        throw new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
            "Simulation engine is temporarily unavailable. Please try again in 30 seconds.");
    }

    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "pythonBridge", fallbackMethod = "modulationComparisonFallback")
    @io.github.resilience4j.timelimiter.annotation.TimeLimiter(name = "pythonBridge")
    public ModulationComparisonResultDto runModulationComparison(ModulationComparisonRequestDto requestParams) {
        try {
            String url = buildSimulateUrl() + "/modulation-comparison";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ModulationComparisonRequestDto> httpRequest = new HttpEntity<>(requestParams, headers);

            ModulationComparisonResultDto dto = restTemplate.postForObject(url, httpRequest, ModulationComparisonResultDto.class);

            if (dto == null) {
                throw new RuntimeException("Python bridge returned an empty response for modulation comparison");
            }

            SimulationResult entity = new SimulationResult();
            entity.setSimulationType("MOD_COMPARISON");
            
            entity.setComparisonSnrMin(BigDecimal.valueOf(dto.getSnr_min()));
            entity.setComparisonSnrMax(BigDecimal.valueOf(dto.getSnr_max()));

            // Store JSON string lists
            entity.setSnrDb(objectMapper.writeValueAsString(dto.getSnr_db()));
            entity.setBpskBer(objectMapper.writeValueAsString(dto.getBpsk()));
            entity.setQpskBer(objectMapper.writeValueAsString(dto.getQpsk()));
            entity.setQam16Ber(objectMapper.writeValueAsString(dto.getQam16()));
            entity.setQam64Ber(objectMapper.writeValueAsString(dto.getQam64()));
            entity.setCrossoverPoints(objectMapper.writeValueAsString(dto.getCrossover_points()));
            
            entity.setHardwareUsed("Mathematical comparison (scipy.special)");
            entity.setTimestamp(LocalDateTime.now());
            entity.setShareToken(java.util.UUID.randomUUID().toString());
            entity.setIsPublic(true);

            simulationResultRepository.save(entity);

            return dto;

        } catch (RestClientException ex) {
            throw new RuntimeException("Could not reach the Python bridge. Details: " + ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Modulation comparison failed: " + ex.getMessage(), ex);
        }
    }

    public ModulationComparisonResultDto modulationComparisonFallback(ModulationComparisonRequestDto requestParams, Throwable t) {
        throw new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
            "Simulation engine is temporarily unavailable. Please try again in 30 seconds.");
    }

    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "pythonBridge", fallbackMethod = "channelCapacityFallback")
    @io.github.resilience4j.timelimiter.annotation.TimeLimiter(name = "pythonBridge")
    public ChannelCapacityResultDto runChannelCapacity(ChannelCapacityRequestDto requestParams) {
        try {
            String url = buildSimulateUrl() + "/channel-capacity";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ChannelCapacityRequestDto> httpRequest = new HttpEntity<>(requestParams, headers);

            ChannelCapacityResultDto dto = restTemplate.postForObject(url, httpRequest, ChannelCapacityResultDto.class);

            if (dto == null) {
                throw new RuntimeException("Python bridge returned an empty response for channel capacity");
            }

            SimulationResult entity = new SimulationResult();
            entity.setSimulationType("CHANNEL_CAPACITY");

            entity.setSnrMin(BigDecimal.valueOf(dto.getSnr_min()));
            entity.setSnrMax(BigDecimal.valueOf(dto.getSnr_max()));
            entity.setSnrDb(objectMapper.writeValueAsString(dto.getSnr_db()));

            entity.setCapacityCurvesJson(objectMapper.writeValueAsString(dto.getCapacity_curves()));
            entity.setSpectralEfficiencyJson(objectMapper.writeValueAsString(dto.getSpectral_efficiency()));
            entity.setInsightsJson(objectMapper.writeValueAsString(dto.getInsights()));

            entity.setHardwareUsed("Mathematical Channel Capacity (Shannon)");
            entity.setTimestamp(LocalDateTime.now());
            entity.setShareToken(java.util.UUID.randomUUID().toString());
            entity.setIsPublic(true);

            simulationResultRepository.save(entity);

            return dto;

        } catch (RestClientException ex) {
            throw new RuntimeException("Could not reach the Python bridge. Details: " + ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Channel capacity generation failed: " + ex.getMessage(), ex);
        }
    }

    public ChannelCapacityResultDto channelCapacityFallback(ChannelCapacityRequestDto requestParams, Throwable t) {
        throw new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
            "Simulation engine is temporarily unavailable. Please try again in 30 seconds.");
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
            dto.setSnr_db(objectMapper.readValue(entity.getSnrDb(), new com.fasterxml.jackson.core.type.TypeReference<List<Double>>() {}));
            dto.setBer_theoretical(objectMapper.readValue(entity.getBerTheoretical(), new com.fasterxml.jackson.core.type.TypeReference<List<Double>>() {}));
            dto.setBer_simulated(objectMapper.readValue(entity.getBerSimulated(), new com.fasterxml.jackson.core.type.TypeReference<List<Double>>() {}));
            dto.setModulation(entity.getModulationType());
            dto.setCode_rate(entity.getCodeRate() != null ? entity.getCodeRate().doubleValue() : null);
            dto.setSimulation_time_ms(entity.getSimulationTimeMs());
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize simulation from DB: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Comparison
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * GET /api/simulations/compare?id1=X&id2=Y
     *
     * Fetches both SimulationResult records by ID, converts to DTOs,
     * and returns a ComparisonResponseDto with overlay metadata.
     */
    public ComparisonResponseDto compareSimulations(Long id1, Long id2) {
        SimulationResult e1 = simulationResultRepository.findById(id1)
                .orElseThrow(() -> new RuntimeException("Simulation not found: " + id1));

        SimulationResult e2 = simulationResultRepository.findById(id2)
                .orElseThrow(() -> new RuntimeException("Simulation not found: " + id2));

        ComparisonResponseDto response = new ComparisonResponseDto();
        response.setSimulation1(SimulationResultDto.from(e1));
        response.setSimulation2(SimulationResultDto.from(e2));
        response.setComparison_metadata(
            new ComparisonResponseDto.ComparisonMetadata(e1.getSimulationType(), e2.getSimulationType()));
        return response;
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
        
        if (base != null && !base.startsWith("http://") && !base.startsWith("https://")) {
            base = "https://" + base;
        }

        // Remove old legacy path suffix if present in the config value
        if (base != null && base.endsWith("/simulate/demo")) {
            base = base.substring(0, base.length() - "/simulate/demo".length());
        } else if (base != null && base.endsWith("/simulate")) {
            base = base.substring(0, base.length() - "/simulate".length());
        }
        
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        
        return base + "/simulate";
    }
}

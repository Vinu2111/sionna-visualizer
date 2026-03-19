package com.sionnavisualizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sionnavisualizer.dto.SimulationDto;
import com.sionnavisualizer.model.SimulationResult;
import com.sionnavisualizer.repository.SimulationResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * SimulationService contains the core business logic for:
 * 1. Calling the Python bridge to run a simulation
 * 2. Saving the result to PostgreSQL
 * 3. Fetching saved simulations from the database
 */
@Service
public class SimulationService {

    // The URL of the Python FastAPI bridge's demo endpoint
    @Value("${python-bridge.url}")
    private String PYTHON_BRIDGE_URL;

    // RestTemplate is Spring's HTTP client — used to make GET/POST calls to other services
    private final RestTemplate restTemplate;

    // The repository handles all database operations for SimulationResult
    private final SimulationResultRepository simulationResultRepository;

    // ObjectMapper converts Java objects to/from JSON strings (used for the snrDb and ber fields)
    private final ObjectMapper objectMapper;

    // Constructor injection — Spring automatically provides these beans when the service starts
    public SimulationService(RestTemplate restTemplate,
                              SimulationResultRepository simulationResultRepository,
                              ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.simulationResultRepository = simulationResultRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Makes an HTTP GET call to the Python bridge, parses the result,
     * saves it to PostgreSQL, and returns the DTO back to the controller.
     *
     * This method acts as the "orchestrator" for the entire data flow:
     * Angular → Java → Python → PostgreSQL → Angular
     */
    public SimulationDto runDemoSimulation() {
        try {
            // Step 1: Call the Python FastAPI bridge and map the JSON response into SimulationDto
            // RestTemplate.getForObject() does the HTTP request AND JSON parsing in one step
            SimulationDto simulationDto = restTemplate.getForObject(PYTHON_BRIDGE_URL, SimulationDto.class);

            if (simulationDto == null) {
                throw new RuntimeException("Python bridge returned an empty response");
            }

            // Step 2: Convert the list arrays to JSON strings for database storage
            // e.g. [0.0, 2.0, 4.0] → "[0.0,2.0,4.0]"
            String snrDbJson = objectMapper.writeValueAsString(simulationDto.getSnr_db());
            String berJson = objectMapper.writeValueAsString(simulationDto.getBer());

            // Step 3: Extract useful fields from the metadata map
            Object numOfdmObj = simulationDto.getMetadata().get("num_ofdm_symbols");
            Object fftSizeObj = simulationDto.getMetadata().get("fft_size");
            Object sionnaUsedObj = simulationDto.getMetadata().get("sionna_used");

            Integer numOfdmSymbols = numOfdmObj != null ? ((Number) numOfdmObj).intValue() : null;
            Integer fftSize = fftSizeObj != null ? ((Number) fftSizeObj).intValue() : null;
            String hardwareUsed = Boolean.TRUE.equals(sionnaUsedObj) ? "NVIDIA Sionna GPU" : "Mock Generation";

            // Step 4: Parse the timestamp string from Python into a LocalDateTime object
            LocalDateTime simulationTimestamp = LocalDateTime.parse(
                simulationDto.getTimestamp(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
            );

            // Step 5: Build the JPA entity and populate all fields
            SimulationResult entity = new SimulationResult();
            entity.setSnrDb(snrDbJson);
            entity.setBer(berJson);
            entity.setNumOfdmSymbols(numOfdmSymbols);
            entity.setFftSize(fftSize);
            entity.setHardwareUsed(hardwareUsed);
            entity.setTimestamp(simulationTimestamp);
            
            // Assign a structurally identical randomly validated string inherently safely protecting identity natively
            entity.setShareToken(java.util.UUID.randomUUID().toString());
            entity.setIsPublic(true);
            // Note: createdAt is set automatically by the @PrePersist hook on the entity

            // Step 6: Save the entity to PostgreSQL
            simulationResultRepository.save(entity);

            // Step 7: Return the original DTO so the controller can send it back to Angular
            return simulationDto;

        } catch (RestClientException exception) {
            // Python bridge is down or unreachable — return a clear error instead of crashing
            throw new RuntimeException("Could not reach the Python bridge at " + PYTHON_BRIDGE_URL +
                    ". Please ensure uvicorn is running. Details: " + exception.getMessage());
        } catch (Exception exception) {
            // Catch-all for JSON parsing errors, database errors, etc.
            throw new RuntimeException("Simulation failed: " + exception.getMessage(), exception);
        }
    }

    /**
     * Retrieves all previously run simulations from PostgreSQL, ordered newest first.
     * The Angular frontend can call this to show a history of simulation runs.
     */
    public List<SimulationResult> getAllSimulations() {
        // Delegates directly to the repository — Spring Data JPA handles the SQL
        return simulationResultRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Finds a logically publicly shared token structurally inherently reliably exclusively natively.
     */
    public SimulationDto getSimulationByShareToken(String token) {
        SimulationResult entity = simulationResultRepository.findByShareToken(token)
            .orElseThrow(() -> new RuntimeException("Simulation absolutely securely effectively entirely completely unmapped."));
            
        if (!entity.getIsPublic()) {
            throw new RuntimeException("Access to this mathematical log remains securely dynamically locally restricted.");
        }
        
        // Reverse process perfectly mirroring JSON decoding cleanly
        SimulationDto dto = new SimulationDto();
        try {
            dto.setSnr_db(objectMapper.readValue(entity.getSnrDb(), List.class));
            dto.setBer(objectMapper.readValue(entity.getBer(), List.class));
            
            java.util.Map<String, Object> meta = new java.util.HashMap<>();
            meta.put("num_ofdm_symbols", entity.getNumOfdmSymbols());
            meta.put("fft_size", entity.getFftSize());
            meta.put("hardware_used", entity.getHardwareUsed());
            dto.setMetadata(meta);
            
            dto.setTimestamp(entity.getTimestamp().toString());
            return dto;
            
        } catch (Exception e) {
            throw new RuntimeException("JSON parsing successfully efficiently unconditionally effectively structurally failed." , e);
        }
    }
}

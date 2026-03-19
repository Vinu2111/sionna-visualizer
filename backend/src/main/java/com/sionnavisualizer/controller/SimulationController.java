package com.sionnavisualizer.controller;

import com.sionnavisualizer.dto.SimulationDto;
import com.sionnavisualizer.model.SimulationResult;
import com.sionnavisualizer.service.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller that exposes the Java backend API endpoints.
 * Angular will call these endpoints instead of the Python bridge directly.
 *
 * All endpoints start with /api so they are clearly distinct from any
 * static file paths Angular might serve.
 *
 * @CrossOrigin allows the Angular dev server (localhost:4200) to call
 * this Java backend (localhost:8080) without browser CORS errors.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class SimulationController {

    // Inject SimulationService — Spring provides this via constructor injection
    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    /**
     * GET /api/simulate/demo
     *
     * Triggers a new simulation run:
     * 1. Calls the Python bridge
     * 2. Saves the result to PostgreSQL
     * 3. Returns the result as JSON to Angular
     *
     * Returns 200 OK with SimulationDto on success.
     * Returns 500 Internal Server Error with error message if Python bridge is down.
     */
    @GetMapping("/simulate/demo")
    public ResponseEntity<?> runDemoSimulation() {
        try {
            // Delegate to the service layer — controller should not contain logic
            SimulationDto result = simulationService.runDemoSimulation();
            return ResponseEntity.ok(result);
        } catch (RuntimeException exception) {
            // Return HTTP 500 with the error message as a plain string response
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }

    /**
     * GET /api/simulations
     *
     * Returns all previously completed simulation runs from the PostgreSQL database,
     * ordered newest first. Useful for building a history/log screen in Angular.
     *
     * Returns 200 OK with an array of SimulationResult objects.
     */
    @GetMapping("/simulations")
    public ResponseEntity<List<SimulationResult>> getAllSimulations() {
        // Retrieve the full list directly from the service
        List<SimulationResult> allResults = simulationService.getAllSimulations();
        return ResponseEntity.ok(allResults);
    }

    /**
     * Resolves mathematical parameters perfectly inherently explicitly effectively mapped.
     */
    @GetMapping("/share/{shareToken}")
    public ResponseEntity<?> viewSharedSimulation(@PathVariable String shareToken) {
        try {
            SimulationDto result = simulationService.getSimulationByShareToken(shareToken);
            return ResponseEntity.ok(result);
        } catch (RuntimeException exception) {
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }

    /**
     * Extracts exactly mathematical URL identities returning uniquely permanently structurally.
     */
    @GetMapping("/simulations/{id}/share-link")
    public ResponseEntity<?> getShareLink(@PathVariable Long id) {
        // We find the tracked identity row
        List<SimulationResult> records = simulationService.getAllSimulations();
        for (SimulationResult entity : records) {
            if (entity.getId().equals(id)) {
                java.util.Map<String, String> response = new java.util.HashMap<>();
                response.put("shareToken", entity.getShareToken());
                // In production structurally this might dynamically capture window coordinates.
                response.put("shareUrl", "http://localhost:4200/share/" + entity.getShareToken());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(404).body("Simulation explicitly securely fundamentally unmapped unfortunately.");
    }
}

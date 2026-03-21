package com.sionnavisualizer.controller;

import com.sionnavisualizer.dto.SimulationDto;
import com.sionnavisualizer.dto.SimulationRequestDto;
import com.sionnavisualizer.dto.BeamPatternRequestDto;
import com.sionnavisualizer.dto.BeamPatternResultDto;
import com.sionnavisualizer.model.SimulationResult;
import com.sionnavisualizer.service.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Sionna simulation API endpoints to the Angular frontend.
 *
 * Endpoints:
 *   GET  /api/simulate/demo            – Quick demo simulation (QPSK defaults)
 *   POST /api/simulate                 – Custom simulation with user parameters
 *   GET  /api/simulations              – Full history from PostgreSQL
 *   GET  /api/share/{token}            – View a publicly shared simulation
 *   GET  /api/simulations/{id}/share-link – Get share URL for a saved run
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = {"https://*.vercel.app", "http://localhost:4200"})
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Simulation endpoints
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * GET /api/simulate/demo
     *
     * Triggers a QPSK rate-1/2 simulation with default parameters.
     * Used for the initial dashboard load — no request body needed.
     */
    @GetMapping("/simulate/demo")
    public ResponseEntity<?> runDemoSimulation() {
        try {
            SimulationDto result = simulationService.runDemoSimulation();
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    /**
     * POST /api/simulate
     *
     * Runs a simulation with user-supplied parameters:
     *   - modulation_order  (2/4/16/64)
     *   - code_rate         (0.1 – 1.0)
     *   - num_bits_per_symbol
     *   - snr_min, snr_max, snr_steps
     *
     * Returns the full SimulationDto with both BER curves.
     * Returns HTTP 500 with a plain error message if the Python bridge fails.
     */
    @PostMapping("/simulate")
    public ResponseEntity<?> runSimulation(@RequestBody SimulationRequestDto request) {
        try {
            SimulationDto result = simulationService.runSimulation(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    /**
     * POST /api/simulations/beam-pattern
     *
     * Runs a ULA beam pattern simulation.
     */
    @PostMapping("/simulations/beam-pattern")
    public ResponseEntity<?> runBeamPattern(@RequestBody BeamPatternRequestDto request) {
        try {
            BeamPatternResultDto result = simulationService.runBeamPattern(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // History / sharing endpoints
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * GET /api/simulations
     *
     * Returns all saved simulation runs from PostgreSQL, newest first.
     */
    @GetMapping("/simulations")
    public ResponseEntity<List<SimulationResult>> getAllSimulations() {
        return ResponseEntity.ok(simulationService.getAllSimulations());
    }

    /**
     * GET /api/share/{shareToken}
     *
     * Fetches a single simulation by its public share token.
     * Returns 404 if the token is unknown or the run is private.
     */
    @GetMapping("/share/{shareToken}")
    public ResponseEntity<?> viewSharedSimulation(@PathVariable String shareToken) {
        try {
            SimulationDto result = simulationService.getSimulationByShareToken(shareToken);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    /**
     * GET /api/simulations/{id}/share-link
     *
     * Returns the public share URL for a saved simulation record.
     */
    @GetMapping("/simulations/{id}/share-link")
    public ResponseEntity<?> getShareLink(@PathVariable Long id) {
        List<SimulationResult> records = simulationService.getAllSimulations();
        for (SimulationResult entity : records) {
            if (entity.getId().equals(id)) {
                java.util.Map<String, String> response = new java.util.HashMap<>();
                response.put("shareToken", entity.getShareToken());
                String frontendBase = System.getenv("FRONTEND_URL") != null
                        ? System.getenv("FRONTEND_URL")
                        : "http://localhost:4200";
                response.put("shareUrl", frontendBase + "/share/" + entity.getShareToken());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(404).body("Simulation not found with id: " + id);
    }
}

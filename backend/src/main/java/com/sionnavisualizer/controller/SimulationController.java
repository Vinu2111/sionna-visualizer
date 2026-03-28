package com.sionnavisualizer.controller;

import com.sionnavisualizer.dto.SimulationDto;
import com.sionnavisualizer.dto.SimulationRequestDto;
import com.sionnavisualizer.dto.BeamPatternRequestDto;
import com.sionnavisualizer.dto.BeamPatternResultDto;
import com.sionnavisualizer.dto.ModulationComparisonRequestDto;
import com.sionnavisualizer.dto.ModulationComparisonResultDto;
import com.sionnavisualizer.dto.ComparisonResponseDto;
import com.sionnavisualizer.dto.ChannelCapacityRequestDto;
import com.sionnavisualizer.dto.ChannelCapacityResultDto;
import com.sionnavisualizer.dto.PathLossRequestDto;
import com.sionnavisualizer.dto.PathLossResultDto;
import com.sionnavisualizer.dto.SimulationEstimateRequestDto;
import com.sionnavisualizer.dto.SimulationEstimateResultDto;
import com.sionnavisualizer.dto.RayDirectionRequestDto;
import com.sionnavisualizer.dto.RayDirectionResultDto;
import com.sionnavisualizer.dto.UeTrajectoryRequestDto;
import com.sionnavisualizer.dto.UeTrajectoryResultDto;
import com.sionnavisualizer.dto.MeasurementOverlayRequestDto;
import com.sionnavisualizer.dto.MeasurementOverlayResultDto;
import com.sionnavisualizer.dto.SinrSteeringRequestDto;
import com.sionnavisualizer.dto.SinrSteeringResultDto;
import com.sionnavisualizer.model.SimulationResult;
import com.sionnavisualizer.service.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

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
    public ResponseEntity<?> runSimulation(@Valid @RequestBody SimulationRequestDto request) {
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
    public ResponseEntity<?> runBeamPattern(@Valid @RequestBody BeamPatternRequestDto request) {
        try {
            BeamPatternResultDto result = simulationService.runBeamPattern(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    /**
     * POST /api/simulations/modulation-comparison
     *
     * Runs theoretical comparison for BPSK, QPSK, 16QAM, 64QAM.
     */
    @PostMapping("/simulations/modulation-comparison")
    public ResponseEntity<?> runModulationComparison(@Valid @RequestBody ModulationComparisonRequestDto request) {
        try {
            ModulationComparisonResultDto result = simulationService.runModulationComparison(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    /**
     * POST /api/simulations/channel-capacity
     *
     * Runs theoretical Shannon channel capacity.
     */
    @PostMapping("/simulations/channel-capacity")
    public ResponseEntity<?> runChannelCapacity(@Valid @RequestBody ChannelCapacityRequestDto request) {
        try {
            ChannelCapacityResultDto result = simulationService.runChannelCapacity(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    /**
     * POST /api/simulations/path-loss
     *
     * Runs path loss generation for multi-ray modeling.
     */
    @PostMapping("/simulations/path-loss")
    public ResponseEntity<?> runPathLoss(@Valid @RequestBody PathLossRequestDto request) {
        try {
            if (request.getNum_paths() != 4 && request.getNum_paths() != 8 && 
                request.getNum_paths() != 16 && request.getNum_paths() != 32) {
                return ResponseEntity.badRequest().body("Number of paths must be 4, 8, 16, or 32");
            }
            PathLossResultDto result = simulationService.runPathLoss(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    /**
     * POST /api/simulations/ray-directions
     *
     * Computes ray paths, departure and arrival angles.
     */
    @PostMapping("/simulations/ray-directions")
    public ResponseEntity<?> runRayDirections(@Valid @RequestBody RayDirectionRequestDto request) {
        try {
            if (request.getNum_paths() != 4 && request.getNum_paths() != 8 && 
                request.getNum_paths() != 16 && request.getNum_paths() != 32) {
                return ResponseEntity.badRequest().body("Number of paths must be 4, 8, 16, or 32");
            }
            if (request.getFrequency_ghz() < 1 || request.getFrequency_ghz() > 100) {
                return ResponseEntity.badRequest().body("Frequency must be between 1 and 100 GHz");
            }
            if (request.getTx_position() == null || request.getTx_position().size() != 3) {
                return ResponseEntity.badRequest().body("tx_position must be an array of exactly 3 values");
            }
            if (request.getRx_position() == null || request.getRx_position().size() != 3) {
                return ResponseEntity.badRequest().body("rx_position must be an array of exactly 3 values");
            }
            
            RayDirectionResultDto result = simulationService.simulateRayDirections(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    /**
     * POST /api/simulations/ue-trajectory
     *
     * Computes UE trajectory moving over coverage map.
     */
    @PostMapping("/simulations/ue-trajectory")
    public ResponseEntity<?> runUeTrajectory(@Valid @RequestBody UeTrajectoryRequestDto request) {
        try {
            UeTrajectoryResultDto result = simulationService.simulateUeTrajectory(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    /**
     * POST /api/simulations/estimate
     *
     * Predicts compute time for a given simulation — no DB write, no auth required.
     */
    @PostMapping("/simulations/estimate")
    public ResponseEntity<?> runEstimate(@Valid @RequestBody SimulationEstimateRequestDto request) {
        try {
            java.util.List<String> validTypes = java.util.Arrays.asList(
                "AWGN", "BEAM_PATTERN", "MODULATION_COMPARISON", "CHANNEL_CAPACITY", "PATH_LOSS", "RAY_DIRECTIONS", "UE_TRAJECTORY"
            );
            if (!validTypes.contains(request.getSimulation_type().toUpperCase())) {
                return ResponseEntity.badRequest().body("Invalid simulation_type. Must be one of: " + validTypes);
            }
            SimulationEstimateResultDto result = simulationService.runEstimate(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    /**
     * POST /api/simulations/measurement-overlay
     *
     * Accepts real BER measurement points and compares them against the simulated curve.
     * Returns calibration quality, RMSE, systematic offset, and per-point error analysis.
     */
    @PostMapping("/simulations/measurement-overlay")
    public ResponseEntity<?> runMeasurementOverlay(@Valid @RequestBody MeasurementOverlayRequestDto request) {
        try {
            // Validate per-point ranges
            for (var point : request.getMeasurements()) {
                if (point.getSnrDb() < -20 || point.getSnrDb() > 40) {
                    return ResponseEntity.badRequest().body("Each snr_db must be between -20 and 40 dB");
                }
                if (point.getBerMeasured() < 0 || point.getBerMeasured() > 1) {
                    return ResponseEntity.badRequest().body("Each ber_measured must be between 0 and 1");
                }
            }
            java.util.List<String> validTypes = java.util.Arrays.asList("AWGN");
            if (!validTypes.contains(request.getSimulationType().toUpperCase())) {
                return ResponseEntity.badRequest().body("simulation_type must be one of: " + validTypes);
            }
            MeasurementOverlayResultDto result = simulationService.runMeasurementOverlay(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    /**
     * POST /api/simulations/sinr-steering
     *
     * Computes SINR, array gain, interference suppression across multiple steering angles.
     */
    @PostMapping("/simulations/sinr-steering")
    public ResponseEntity<?> runSinrSteering(@Valid @RequestBody SinrSteeringRequestDto request) {
        try {
            int[] validAntennas = {8, 16, 32, 64};
            boolean validN = false;
            for (int n : validAntennas) { if (n == request.getNumAntennas()) validN = true; }
            if (!validN) {
                return ResponseEntity.badRequest().body("num_antennas must be 8, 16, 32, or 64");
            }
            if (request.getSteeringAngles() == null || request.getSteeringAngles().size() < 1 || request.getSteeringAngles().size() > 20) {
                return ResponseEntity.badRequest().body("steering_angles must have 1 to 20 angles");
            }
            for (double angle : request.getSteeringAngles()) {
                if (angle < -90 || angle > 90) {
                    return ResponseEntity.badRequest().body("Each steering angle must be between -90 and 90 degrees");
                }
            }
            if (request.getFrequencyGhz() < 1 || request.getFrequencyGhz() > 100) {
                return ResponseEntity.badRequest().body("frequency_ghz must be between 1 and 100");
            }
            SinrSteeringResultDto result = simulationService.runSinrSteering(request);
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

    /**
     * GET /api/simulations/compare?id1=X&id2=Y
     *
     * Returns both simulation records and overlay metadata.
     * Used by the Angular compare page to build the overlay chart.
     */
    @GetMapping("/simulations/compare")
    public ResponseEntity<?> compareSimulations(
            @RequestParam Long id1,
            @RequestParam Long id2) {
        try {
            ComparisonResponseDto result = simulationService.compareSimulations(id1, id2);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    /**
     * GET /api/simulations/colormaps
     *
     * Returns the list of available chart colormaps.
     */
    @GetMapping("/simulations/colormaps")
    public ResponseEntity<?> getColormaps() {
        try {
            return ResponseEntity.ok(simulationService.getColormaps());
        } catch (RuntimeException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }
}

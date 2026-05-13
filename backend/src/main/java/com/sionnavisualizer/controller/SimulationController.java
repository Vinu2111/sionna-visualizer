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
import com.sionnavisualizer.dto.PathDto;
import com.sionnavisualizer.dto.PathLossSummaryDto;
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
import com.sionnavisualizer.dto.PerformanceDto;
import com.sionnavisualizer.model.SimulationResult;
import com.sionnavisualizer.service.SimulationService;
import com.sionnavisualizer.service.BerMathEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * REST Controller exposing Sionna simulation API endpoints to the Angular frontend.
 *
 * Endpoints:
 *   GET  /api/simulate/demo            – Quick demo simulation (QPSK defaults)
 *   POST /api/simulate                 – Custom simulation with user parameters
 *   GET  /api/simulations              – Full history from PostgreSQL
 *   GET  /api/share/{token}            – View a publicly shared simulation
 *   GET  /api/simulations/{id}/share-link – Get share URL for a saved run
 *   GET  /api/public/simulate/preview  – Public preview for landing page demo
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = {"https://*.vercel.app", "http://localhost:4200"})
public class SimulationController {

    private static final Logger log = LoggerFactory.getLogger(SimulationController.class);

    private final SimulationService simulationService;
    private final BerMathEngine berMathEngine;

    public SimulationController(SimulationService simulationService, BerMathEngine berMathEngine) {
        this.simulationService = simulationService;
        this.berMathEngine = berMathEngine;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper: convert modulation_order integer to modulation name string
    // ─────────────────────────────────────────────────────────────────────────
    private String modulationNameFromOrder(int order) {
        switch (order) {
            case 2:  return "BPSK";
            case 4:  return "QPSK";
            case 16: return "16QAM";
            case 64: return "64QAM";
            default: return "QPSK";
        }
    }

    // Helper: convert double[] to List<Double>
    private List<Double> toList(double[] arr) {
        return DoubleStream.of(arr).boxed().collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Simulation endpoints
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * GET /api/simulate/demo
     *
     * Triggers a QPSK rate-1/2 simulation with default parameters.
     * Used for the initial dashboard load — no request body needed.
     * Falls back to BerMathEngine if Python bridge is unavailable.
     */
    @GetMapping("/simulate/demo")
    public ResponseEntity<?> runDemoSimulation() {
        try {
            SimulationDto result = simulationService.runDemoSimulation();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.warn("Python bridge unavailable for demo, using BerMathEngine: {}", e.getMessage());

            // Generate real QPSK BER values using BerMathEngine
            double[] snrRange = berMathEngine.generateSnrRange(-10, 30, 41);
            double[] theoretical = berMathEngine.calculateTheoreticalBer("QPSK", snrRange);
            double[] simulated = berMathEngine.calculateSimulatedBer("QPSK", snrRange);

            SimulationDto dto = new SimulationDto();
            dto.setSnr_db(toList(snrRange));
            dto.setBer_theoretical(toList(theoretical));
            dto.setBer_simulated(toList(simulated));
            dto.setModulation("QPSK");
            dto.setCode_rate(0.5);
            dto.setSimulation_time_ms(0);
            dto.setNum_bits_simulated(0);
            dto.setColormap_used("default");
            dto.setSource("java_math_engine");

            return ResponseEntity.ok(dto);
        }
    }

    /**
     * POST /api/simulate
     *
     * Runs a simulation with user-supplied parameters.
     * Falls back to BerMathEngine when the Python bridge is unavailable.
     */
    @PostMapping("/simulate")
    public ResponseEntity<?> runSimulation(@Valid @RequestBody SimulationRequestDto request) {
        try {
            SimulationDto result = simulationService.runSimulation(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            log.warn("Python bridge unavailable for simulation, falling back to BerMathEngine: {}", ex.getMessage());

            // Extract parameters from request
            String modulation = modulationNameFromOrder(request.getModulation_order());
            double[] snrRange = berMathEngine.generateSnrRange(
                request.getSnr_min(), request.getSnr_max(), request.getSnr_steps()
            );
            double[] theoretical = berMathEngine.calculateTheoreticalBer(modulation, snrRange);
            double[] simulated = berMathEngine.calculateSimulatedBer(modulation, snrRange);

            SimulationDto dto = new SimulationDto();
            dto.setSnr_db(toList(snrRange));
            dto.setBer_theoretical(toList(theoretical));
            dto.setBer_simulated(toList(simulated));
            dto.setModulation(modulation);
            dto.setCode_rate(request.getCode_rate());
            dto.setSimulation_time_ms(0);
            dto.setNum_bits_simulated(0);
            dto.setColormap_used("default");
            dto.setSource("java_math_engine");

            return ResponseEntity.ok(dto);
        }
    }

    /**
     * POST /api/simulations/beam-pattern
     *
     * Runs a ULA beam pattern simulation.
     * Falls back to BerMathEngine if Python bridge is unavailable.
     */
    @PostMapping("/simulations/beam-pattern")
    public ResponseEntity<?> runBeamPattern(@Valid @RequestBody BeamPatternRequestDto request) {
        try {
            BeamPatternResultDto result = simulationService.runBeamPattern(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            log.warn("Python bridge unavailable for beam-pattern, falling back to BerMathEngine: {}", ex.getMessage());

            Map<String, Object> beamData = berMathEngine.calculateBeamPattern(
                request.getNum_antennas(),
                request.getFrequency_ghz(),
                request.getSteering_angle()
            );

            double[] angles = (double[]) beamData.get("angles");
            double[] patternDb = (double[]) beamData.get("pattern_db");

            BeamPatternResultDto dto = new BeamPatternResultDto();
            dto.setAngles(toList(angles));
            dto.setPattern_db(toList(patternDb));
            dto.setSteering_angle(request.getSteering_angle());
            dto.setNum_antennas(request.getNum_antennas());
            dto.setFrequency_ghz(request.getFrequency_ghz());

            // Approximate main lobe width ~ 102 / N degrees for half-wave spacing
            dto.setMain_lobe_width(102.0 / request.getNum_antennas());
            dto.setSide_lobe_level(-13.2); // Typical first sidelobe for uniform linear array
            dto.setArray_gain_db(10 * Math.log10(request.getNum_antennas()));
            dto.setColormap_used("default");

            return ResponseEntity.ok(dto);
        }
    }

    /**
     * POST /api/simulations/modulation-comparison
     *
     * Runs theoretical comparison for BPSK, QPSK, 16QAM, 64QAM.
     * Falls back to BerMathEngine if Python bridge is unavailable.
     */
    @PostMapping("/simulations/modulation-comparison")
    public ResponseEntity<?> runModulationComparison(@Valid @RequestBody ModulationComparisonRequestDto request) {
        try {
            ModulationComparisonResultDto result = simulationService.runModulationComparison(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            log.warn("Python bridge unavailable for modulation-comparison, falling back to BerMathEngine: {}", ex.getMessage());

            double[] snrRange = berMathEngine.generateSnrRange(
                request.getSnr_min(), request.getSnr_max(), request.getSnr_steps()
            );

            ModulationComparisonResultDto dto = new ModulationComparisonResultDto();
            dto.setSnr_db(toList(snrRange));
            dto.setBpsk(toList(berMathEngine.calculateTheoreticalBer("BPSK", snrRange)));
            dto.setQpsk(toList(berMathEngine.calculateTheoreticalBer("QPSK", snrRange)));
            dto.setQam16(toList(berMathEngine.calculateTheoreticalBer("16QAM", snrRange)));
            dto.setQam64(toList(berMathEngine.calculateTheoreticalBer("64QAM", snrRange)));
            dto.setSnr_min(request.getSnr_min());
            dto.setSnr_max(request.getSnr_max());
            dto.setSnr_steps(request.getSnr_steps());
            dto.setColormap_used("default");

            return ResponseEntity.ok(dto);
        }
    }

    /**
     * POST /api/simulations/channel-capacity
     *
     * Runs theoretical Shannon channel capacity.
     * Falls back to BerMathEngine if Python bridge is unavailable.
     */
    @PostMapping("/simulations/channel-capacity")
    public ResponseEntity<?> runChannelCapacity(@Valid @RequestBody ChannelCapacityRequestDto request) {
        try {
            ChannelCapacityResultDto result = simulationService.runChannelCapacity(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            log.warn("Python bridge unavailable for channel-capacity, falling back to BerMathEngine: {}", ex.getMessage());

            double[] snrRange = berMathEngine.generateSnrRange(
                request.getSnr_min(), request.getSnr_max(), request.getSnr_steps()
            );

            List<Map<String, Object>> capacityCurves = new ArrayList<>();
            for (Double bw : request.getBandwidths_mhz()) {
                double[] capacity = berMathEngine.calculateChannelCapacity(snrRange, bw);
                Map<String, Object> curve = new HashMap<>();
                curve.put("bandwidth_mhz", bw);
                curve.put("capacity_mbps", toList(capacity));
                capacityCurves.add(curve);
            }

            // Spectral efficiency (bandwidth-independent Shannon limit)
            double[] spectralEff = new double[snrRange.length];
            for (int i = 0; i < snrRange.length; i++) {
                double snrLin = Math.pow(10, snrRange[i] / 10.0);
                spectralEff[i] = Math.log(1 + snrLin) / Math.log(2);
            }

            ChannelCapacityResultDto dto = new ChannelCapacityResultDto();
            dto.setSnr_db(toList(snrRange));
            dto.setSpectral_efficiency(toList(spectralEff));
            dto.setCapacity_curves(capacityCurves);
            dto.setSnr_min(request.getSnr_min());
            dto.setSnr_max(request.getSnr_max());
            dto.setColormap_used("default");

            return ResponseEntity.ok(dto);
        }
    }

    /**
     * POST /api/simulations/path-loss
     *
     * Runs path loss generation for multi-ray modeling.
     * Falls back to BerMathEngine if Python bridge is unavailable.
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
            log.warn("Python bridge unavailable for path-loss, falling back to BerMathEngine: {}", ex.getMessage());

            int numPaths = request.getNum_paths() != null ? request.getNum_paths() : 4;
            double freqGhz = request.getFrequency_ghz() != null ? request.getFrequency_ghz() : 28.0;
            
            List<PathDto> paths = new ArrayList<>();
            java.util.Random rng = new java.util.Random();
            double minLoss = Double.MAX_VALUE;
            double maxLoss = Double.MIN_VALUE;
            double sumLoss = 0;

            for (int p = 0; p < numPaths; p++) {
                double distance = 50 + rng.nextDouble() * 950; // 50m to 1000m
                double pathLoss = berMathEngine.calculatePathLoss(distance, freqGhz);
                // Add random fading margin
                pathLoss += rng.nextGaussian() * 3.0;

                PathDto pathDto = new PathDto();
                pathDto.setPath_id(p);
                pathDto.setDistance_m(Math.round(distance * 100.0) / 100.0);
                pathDto.setPath_loss_db(Math.round(pathLoss * 100.0) / 100.0);
                pathDto.setPath_type(p == 0 ? "LoS" : "NLoS");
                pathDto.setDelay_ns(Math.round(distance / 0.3 * 100.0) / 100.0); // speed of light approx
                paths.add(pathDto);

                minLoss = Math.min(minLoss, pathLoss);
                maxLoss = Math.max(maxLoss, pathLoss);
                sumLoss += pathLoss;
            }

            PathLossSummaryDto summary = new PathLossSummaryDto();
            summary.setLos_path_loss_db(Math.round(minLoss * 100.0) / 100.0);
            summary.setMax_path_loss_db(Math.round(maxLoss * 100.0) / 100.0);
            summary.setPath_loss_spread_db(Math.round((maxLoss - minLoss) * 100.0) / 100.0);
            summary.setMean_delay_ns(Math.round((sumLoss / numPaths) * 100.0) / 100.0);

            PathLossResultDto dto = new PathLossResultDto();
            dto.setPaths(paths);
            dto.setSummary(summary);
            dto.setColormap_used("default");

            return ResponseEntity.ok(dto);
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
    // Public preview endpoint for landing page demo (NO AUTH)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * GET /api/public/simulate/preview
     *
     * Returns BER data using BerMathEngine — no authentication required.
     * Used by the landing page interactive demo chart.
     */
    @GetMapping("/public/simulate/preview")
    public ResponseEntity<?> previewSimulation(
            @RequestParam(defaultValue = "QPSK") String modulation,
            @RequestParam(defaultValue = "-10") double snrMin,
            @RequestParam(defaultValue = "30") double snrMax) {

        // Clamp parameters to safe ranges
        snrMin = Math.max(-20, Math.min(snrMin, 10));
        snrMax = Math.max(snrMin + 5, Math.min(snrMax, 40));

        double[] snrRange = berMathEngine.generateSnrRange(snrMin, snrMax, 41);
        double[] theoretical = berMathEngine.calculateTheoreticalBer(modulation, snrRange);
        double[] simulated = berMathEngine.calculateSimulatedBer(modulation, snrRange);

        Map<String, Object> response = new HashMap<>();
        response.put("modulation", modulation.toUpperCase());
        response.put("snr_db", toList(snrRange));
        response.put("ber_theoretical", toList(theoretical));
        response.put("ber_simulated", toList(simulated));
        response.put("source", "java_math_engine");
        response.put("simulation_time_ms", 0);

        return ResponseEntity.ok(response);
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

package com.sionnavisualizer.controller;

import com.sionnavisualizer.dto.BeamPatternRequestDto;
import com.sionnavisualizer.dto.BeamPatternResultDto;
import com.sionnavisualizer.dto.ModulationComparisonRequestDto;
import com.sionnavisualizer.dto.ModulationComparisonResultDto;
import com.sionnavisualizer.dto.SimulationDto;
import com.sionnavisualizer.dto.SimulationRequestDto;
import com.sionnavisualizer.dto.ChannelCapacityRequestDto;
import com.sionnavisualizer.dto.ChannelCapacityResultDto;
import com.sionnavisualizer.service.SimulationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
@CrossOrigin(originPatterns = {"https://*.vercel.app", "http://localhost:4200"})
public class PublicApiController {

    private final SimulationService simulationService;

    public PublicApiController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/simulate/ber-snr")
    public ResponseEntity<?> runBerSnr(@Valid @RequestBody SimulationRequestDto request) {
        try {
            SimulationDto result = simulationService.runSimulation(request);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/simulate/beam-pattern")
    public ResponseEntity<?> runBeamPattern(@Valid @RequestBody BeamPatternRequestDto request) {
        try {
            BeamPatternResultDto result = simulationService.runBeamPattern(request);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/simulate/modulation-comparison")
    public ResponseEntity<?> runModulationComparison(@Valid @RequestBody ModulationComparisonRequestDto request) {
        try {
            ModulationComparisonResultDto result = simulationService.runModulationComparison(request);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/simulate/channel-capacity")
    public ResponseEntity<?> runChannelCapacity(@Valid @RequestBody ChannelCapacityRequestDto request) {
        try {
            ChannelCapacityResultDto result = simulationService.runChannelCapacity(request);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "operational",
            "version", "1.0.0",
            "simulations_available", List.of("ber-snr", "beam-pattern", "modulation-comparison", "channel-capacity"),
            "docs", "https://github.com/Vinu2111/sionna-visualizer"
        ));
    }
}

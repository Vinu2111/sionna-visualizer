package com.sionnavisualizer.controller;

import com.sionnavisualizer.dto.AiExplanationResponse;
import com.sionnavisualizer.dto.AnomalyReportResponse;
import com.sionnavisualizer.service.AnomalyDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/anomaly")
public class AnomalyDetectionController {

    @Autowired
    private AnomalyDetectionService anomalyService;

    private final Long MOCK_USER_ID = 1L;

    /**
     * Runs the full physics-based anomaly analysis pipeline on a simulation's BER curve.
     * This is triggered automatically after simulation or manually via "Run Anomaly Check".
     */
    @PostMapping("/analyze/{simulationId}")
    public ResponseEntity<?> analyzeSimulation(@PathVariable Long simulationId) {
        try {
            AnomalyReportResponse report = anomalyService.analyzeSimulation(simulationId, MOCK_USER_ID);
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Analysis failed: " + e.getMessage()));
        }
    }

    /**
     * Returns the most recently saved anomaly report for a simulation without re-running checks.
     */
    @GetMapping("/report/{simulationId}")
    public ResponseEntity<?> getSavedReport(@PathVariable Long simulationId) {
        AnomalyReportResponse report = anomalyService.getSavedReport(simulationId);
        if (report == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(report);
    }

    /**
     * Calls Claude AI to generate a detailed plain-English explanation of a specific anomaly.
     * Saves the explanation so subsequent calls serve from the database without re-billing Claude.
     */
    @PostMapping("/{anomalyId}/explain")
    public ResponseEntity<?> explainAnomaly(@PathVariable Long anomalyId) {
        try {
            AiExplanationResponse explanation = anomalyService.explainAnomaly(anomalyId, MOCK_USER_ID);
            return ResponseEntity.ok(explanation);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "AI explanation failed: " + e.getMessage()));
        }
    }
}

package com.sionnavisualizer.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sionnavisualizer.dto.ComparisonResponse;
import com.sionnavisualizer.dto.ComparisonStats;
import com.sionnavisualizer.dto.CsvParseResult;
import com.sionnavisualizer.dto.InterpolatedData;
import com.sionnavisualizer.model.MultiSimComparison;
import com.sionnavisualizer.model.SimulationResult;
import com.sionnavisualizer.repository.MultiSimComparisonRepository;
import com.sionnavisualizer.service.MultiSimulatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/compare")
public class MultiSimulatorController {

    @Autowired
    private MultiSimulatorService multiSimulatorService;

    @Autowired
    private MultiSimComparisonRepository comparisonRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Handles the primary upload logic coordinating multi-platform geometry overlaps synchronously
    @PostMapping("/multi-simulator")
    public ResponseEntity<ComparisonResponse> createComparison(
            @RequestParam("sionnaSimulationId") Long simulationId,
            @RequestParam("simulatorType") String simulatorType,
            @RequestParam("csvFile") MultipartFile csvFile) {
        
        try {
            // STEP 1: Process physical CSV uploads structurally bypassing manual parsing
            CsvParseResult externalData = multiSimulatorService.parseCsvFile(csvFile, simulatorType);
            
            // STEP 2: Extract internal baseline 
            SimulationResult sionnaSim = multiSimulatorService.fetchSionnaSimulation(simulationId);
            List<Double> sionnaSnr = objectMapper.readValue(sionnaSim.getSnrDb(), new TypeReference<List<Double>>(){});
            List<Double> sionnaBer = objectMapper.readValue(sionnaSim.getBerSimulated(), new TypeReference<List<Double>>(){});

            // STEP 3: Normalize and crop matching thresholds exclusively logically 
            InterpolatedData data = multiSimulatorService.interpolateToCommonSnrPoints(
                sionnaSnr, sionnaBer, externalData.getSnrValues(), externalData.getBerValues()
            );

            // STEP 4: Measure statistics dynamically reflecting physical simulation structures securely 
            ComparisonStats stats = multiSimulatorService.calculateStatistics(data, simulatorType);

            // Optional: Map throughput
            List<Double> sThroughput = null;
            if (externalData.isHasThroughput()) {
                 // Build mock throughput scaled off SNR explicitly since Sionna natively doesn't simulate full stack protocols out of the box dynamically yet
                 sThroughput = data.getMatchedSnrPoints().stream().map(s -> Math.max(1.0, s * 10.0)).collect(Collectors.toList());
            }

            // STEP 5: Audit standard trace history correctly mapped natively locally
            MultiSimComparison saved = multiSimulatorService.saveComparison(
                 simulationId, simulatorType, data, stats, sThroughput, externalData.getThroughputValues()
            );

            // Compile visual mapping securely
            ComparisonResponse res = new ComparisonResponse();
            res.setComparisonId(saved.getId());
            res.setSionnaSimulationId(simulationId);
            res.setSimulatorType(simulatorType);
            res.setSnrPoints(data.getMatchedSnrPoints());
            res.setSionnaBer(data.getSionnaBer());
            res.setExternalBer(data.getExternalBer());
            res.setBerCrossoverSnr(stats.getBerCrossoverSnr());
            res.setAverageBerDifference(stats.getAverageBerDifference());
            res.setBetterPerformerAt20db(stats.getBetterPerformerAt20db());
            res.setMatchedDataPoints(stats.getMatchedDataPoints());
            
            if (sThroughput != null) {
                res.setSionnaThroughput(sThroughput);
                res.setExternalThroughput(externalData.getThroughputValues());
            }

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{comparisonId}")
    public ResponseEntity<MultiSimComparison> getComparison(@PathVariable Long comparisonId) {
        return comparisonRepository.findById(comparisonId)
             .map(ResponseEntity::ok)
             .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    public ResponseEntity<List<MultiSimComparison>> getHistory() {
        Long userId = 1L; // Mock user resolution
        return ResponseEntity.ok(comparisonRepository.findByUserId(userId));
    }
}

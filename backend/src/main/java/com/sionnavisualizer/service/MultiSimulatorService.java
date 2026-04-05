package com.sionnavisualizer.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sionnavisualizer.dto.ComparisonStats;
import com.sionnavisualizer.dto.CsvParseResult;
import com.sionnavisualizer.dto.InterpolatedData;
import com.sionnavisualizer.model.MultiSimComparison;
import com.sionnavisualizer.model.SimulationResult;
import com.sionnavisualizer.repository.MultiSimComparisonRepository;
import com.sionnavisualizer.repository.SimulationResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class MultiSimulatorService {

    @Autowired
    private SimulationResultRepository simulationResultRepository;

    @Autowired
    private MultiSimComparisonRepository comparisonRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // STEP 1: Parse the external simulator CSV line by line extracting standardized columns
    // We expect SNR_dB and BER structurally minimum, throughput is optional.
    public CsvParseResult parseCsvFile(MultipartFile csvFile, String simulatorType) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8));
        String headerLine = reader.readLine();
        if (headerLine == null) throw new IllegalArgumentException("CSV file is empty.");

        String[] headers = headerLine.split(",");
        int snrIdx = -1, berIdx = -1, thruIdx = -1;
        
        List<String> columns = new ArrayList<>();
        for (int i = 0; i < headers.length; i++) {
            String h = headers[i].trim().toLowerCase();
            if (h.contains("snr")) snrIdx = i;
            if (h.contains("ber")) berIdx = i;
            if (h.contains("throughput")) thruIdx = i;
            columns.add(headers[i].trim());
        }

        if (snrIdx == -1 || berIdx == -1) {
            throw new IllegalArgumentException("CSV must contain columns representing SNR and BER.");
        }

        List<Double> snrValues = new ArrayList<>();
        List<Double> berValues = new ArrayList<>();
        List<Double> thruValues = new ArrayList<>();

        String row;
        while ((row = reader.readLine()) != null) {
            String[] parts = row.split(",");
            if (parts.length <= Math.max(snrIdx, berIdx)) continue;
            snrValues.add(Double.parseDouble(parts[snrIdx].trim()));
            berValues.add(Double.parseDouble(parts[berIdx].trim()));
            if (thruIdx != -1 && parts.length > thruIdx) {
                thruValues.add(Double.parseDouble(parts[thruIdx].trim()));
            }
        }

        CsvParseResult res = new CsvParseResult();
        res.setSimulatorType(simulatorType);
        res.setSnrValues(snrValues);
        res.setBerValues(berValues);
        res.setThroughputValues(thruValues);
        res.setDetectedColumns(columns);
        res.setHasThroughput(!thruValues.isEmpty());
        return res;
    }

    // STEP 2: Extract standard internal Sionna simulation tracking securely natively  
    public SimulationResult fetchSionnaSimulation(Long simulationId) {
        return simulationResultRepository.findById(simulationId)
            .orElseThrow(() -> new IllegalArgumentException("Simulation not found"));
    }

    // STEP 3: Ensure data graphs perfectly overlay matching the exact same specific data constraints logically seamlessly 
    public InterpolatedData interpolateToCommonSnrPoints(
            List<Double> sionnaSnr, List<Double> sionnaBer,
            List<Double> externalSnr, List<Double> externalBer) {
        
        List<Double> matchedSnrPoints = new ArrayList<>();
        List<Double> matchedSionnaBer = new ArrayList<>();
        List<Double> matchedExtBer = new ArrayList<>();

        // Match exact SNR index thresholds (ignoring complex curve fitting interpolation for simplicity)
        for (int i = 0; i < sionnaSnr.size(); i++) {
            double sSnr = sionnaSnr.get(i);
            int extIdx = externalSnr.indexOf(sSnr);
            if (extIdx != -1) {
                matchedSnrPoints.add(sSnr);
                matchedSionnaBer.add(sionnaBer.get(i));
                matchedExtBer.add(externalBer.get(extIdx));
            }
        }

        InterpolatedData data = new InterpolatedData();
        data.setMatchedSnrPoints(matchedSnrPoints);
        data.setSionnaBer(matchedSionnaBer);
        data.setExternalBer(matchedExtBer);
        return data;
    }

    // STEP 4: Compute physical data discrepancy analysis statistics across overlapping sequences natively
    public ComparisonStats calculateStatistics(InterpolatedData data, String simulatorType) {
        List<Double> snr = data.getMatchedSnrPoints();
        List<Double> sBer = data.getSionnaBer();
        List<Double> eBer = data.getExternalBer();

        double crossover = -999.0;
        double sumDiff = 0.0;
        double sBerAt20 = 1.0;
        double eBerAt20 = 1.0;

        for (int i = 0; i < snr.size(); i++) {
            double diff = Math.abs(sBer.get(i) - eBer.get(i));
            sumDiff += diff;

            // Find lowest SNR where error hits standard benchmark 0.001 
            if (crossover == -999.0 && (sBer.get(i) <= 0.001 || eBer.get(i) <= 0.001)) {
                crossover = snr.get(i);
            }

            // Find value at 20dB natively
            if (Math.abs(snr.get(i) - 20.0) < 1.0) {
                sBerAt20 = sBer.get(i);
                eBerAt20 = eBer.get(i);
            }
        }

        ComparisonStats stats = new ComparisonStats();
        stats.setMatchedDataPoints(snr.size());
        stats.setBerCrossoverSnr(crossover);
        stats.setAverageBerDifference(snr.isEmpty() ? 0.0 : (sumDiff / snr.size()) * 100.0);
        
        if (sBerAt20 < eBerAt20) {
            stats.setBetterPerformerAt20db("Sionna");
        } else if (eBerAt20 < sBerAt20) {
            stats.setBetterPerformerAt20db(simulatorType);
        } else {
            stats.setBetterPerformerAt20db("Tie");
        }
        return stats;
    }

    // STEP 5: Parse, execute, and cleanly serialize the comparison metrics structurally into Postgres
    public MultiSimComparison saveComparison(Long simulationId, String simulatorType, InterpolatedData data, 
            ComparisonStats stats, List<Double> sThroughput, List<Double> eThroughput) throws Exception {
        
        MultiSimComparison comp = new MultiSimComparison();
        comp.setSionnaSimulationId(simulationId);
        comp.setSimulatorType(simulatorType);
        comp.setSnrPoints(objectMapper.writeValueAsString(data.getMatchedSnrPoints()));
        comp.setSionnaBer(objectMapper.writeValueAsString(data.getSionnaBer()));
        comp.setExternalBer(objectMapper.writeValueAsString(data.getExternalBer()));
        
        if (sThroughput != null && eThroughput != null) {
            comp.setSionnaThroughput(objectMapper.writeValueAsString(sThroughput));
            comp.setExternalThroughput(objectMapper.writeValueAsString(eThroughput));
        }

        comp.setBerCrossoverSnr(stats.getBerCrossoverSnr());
        comp.setAverageBerDifference(stats.getAverageBerDifference());
        comp.setBetterPerformerAt20db(stats.getBetterPerformerAt20db());
        comp.setMatchedDataPoints(stats.getMatchedDataPoints());
        
        Long userId = 1L; // standard placeholder 
        comp.setUserId(userId);
        
        return comparisonRepository.save(comp);
    }
}

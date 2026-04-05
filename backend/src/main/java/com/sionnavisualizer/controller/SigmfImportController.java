package com.sionnavisualizer.controller;

import com.sionnavisualizer.dto.PythonAnalysisResult;
import com.sionnavisualizer.dto.SigmfImportResponse;
import com.sionnavisualizer.dto.SigmfMetadata;
import com.sionnavisualizer.service.SigmfImportService;
import com.sionnavisualizer.model.ChannelModelResult;
import com.sionnavisualizer.repository.ChannelModelRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/sigmf")
public class SigmfImportController {

    @Autowired
    private SigmfImportService sigmfImportService;

    @Autowired
    private ChannelModelRepository channelModelRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    // Handles strictly separated dual-payload uploads containing separated binary buffers dynamically natively   
    @PostMapping("/import")
    public ResponseEntity<SigmfImportResponse> importSigmf(
            @RequestParam("metaFile") MultipartFile metaFile,
            @RequestParam("dataFile") MultipartFile dataFile,
            @RequestParam("simulationId") Long simulationId) {
        
        try {
            // Step 1: Decode pure JSON layout mappings securely 
            SigmfMetadata metadata = sigmfImportService.parseSigmfMeta(metaFile);

            // Step 2-3: Heavily offload binary parsing onto Python bridges ensuring Java stays non-blocking
            PythonAnalysisResult pythonRes = sigmfImportService.sendToPythonBridge(metaFile, dataFile);

            // Step 4: Extract base simulation logic for comparison checks securely
            ChannelModelResult simRecord = channelModelRepository.findById(simulationId).orElse(null);
            List<Double> simulatedBer = null;
            if (simRecord != null && simRecord.getBerValues() != null) {
                 simulatedBer = objectMapper.readValue(simRecord.getBerValues(), new TypeReference<List<Double>>(){});
            }

            // Step 5: Process theoretical vs actual matching securely safely mapping errors natively
            double matchPerc = sigmfImportService.calculateBerMatch(pythonRes.getBerEstimate(), simulatedBer);

            // Step 6: Trigger DB inserts logging the historical metrics structurally  
            Long userId = 1L; // Simulated auth user mapping structure natively
            sigmfImportService.saveImportRecord(simulationId, metadata, pythonRes.getEstimatedSnrDb(), matchPerc, userId);

            // Step 7: Push the structural wrapper back towards Angular pipelines securely 
            SigmfImportResponse response = new SigmfImportResponse();
            response.setMetadata(metadata);
            response.setEstimatedSnrDb(pythonRes.getEstimatedSnrDb());
            response.setBerEstimate(pythonRes.getBerEstimate());
            response.setSnrRange(pythonRes.getSnrRange());
            response.setIqSamplesI(pythonRes.getIqSamplesI());
            response.setIqSamplesQ(pythonRes.getIqSamplesQ());
            response.setBerMatchPercentage(matchPerc);
            response.setSimulatedBer(simulatedBer);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

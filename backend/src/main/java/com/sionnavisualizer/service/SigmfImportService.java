package com.sionnavisualizer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sionnavisualizer.dto.PythonAnalysisResult;
import com.sionnavisualizer.dto.SigmfMetadata;
import com.sionnavisualizer.model.SigmfImportRecord;
import com.sionnavisualizer.repository.SigmfImportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Service
public class SigmfImportService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SigmfImportRepository sigmfImportRepository;

    @Value("${python.bridge.url:http://localhost:8000}")
    private String pythonBridgeUrl;

    // Synchronously reads the JSON metadata header payload extracting required signal metadata definitions into DTO objects
    public SigmfMetadata parseSigmfMeta(MultipartFile metaFile) throws Exception {
        String jsonStr = new String(metaFile.getBytes(), StandardCharsets.UTF_8);
        JsonNode root = objectMapper.readTree(jsonStr);
        
        SigmfMetadata meta = new SigmfMetadata();
        
        if (root.has("global")) {
            JsonNode global = root.get("global");
            meta.setSampleRate(global.has("core:sample_rate") ? global.get("core:sample_rate").asDouble() : 0.0);
            meta.setDataType(global.has("core:datatype") ? global.get("core:datatype").asText() : "unknown");
            meta.setDescription(global.has("core:description") ? global.get("core:description").asText() : "");
            meta.setHardware(global.has("core:hw") ? global.get("core:hw").asText() : "");
            meta.setAuthor(global.has("core:author") ? global.get("core:author").asText() : "");
        }
        
        if (root.has("captures") && root.get("captures").isArray() && root.get("captures").size() > 0) {
            JsonNode capture = root.get("captures").get(0);
            meta.setCenterFrequency(capture.has("core:frequency") ? capture.get("core:frequency").asDouble() : 0.0);
        }
        
        return meta;
    }

    // Transfers binary and JSON resources actively to Python for intensive IQ signal logic extraction
    public PythonAnalysisResult sendToPythonBridge(MultipartFile metaFile, MultipartFile dataFile) throws Exception {
        String url = pythonBridgeUrl + "/analyze/sigmf";
        
        // Use standard MultiValueMap explicitly handling multi-part form payloads automatically mapping File resources
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        body.add("meta_file", new ByteArrayResource(metaFile.getBytes()) {
            @Override
            public String getFilename() { return metaFile.getOriginalFilename(); }
        });
        
        body.add("data_file", new ByteArrayResource(dataFile.getBytes()) {
            @Override
            public String getFilename() { return dataFile.getOriginalFilename(); }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, requestEntity, PythonAnalysisResult.class);
    }

    // Calculates the overlap correlation identifying how closely standard theory mappings overlay real constraints
    public double calculateBerMatch(java.util.List<Double> hardwareBer, java.util.List<Double> simulatedBer) {
        if (hardwareBer == null || simulatedBer == null || hardwareBer.isEmpty() || simulatedBer.isEmpty()) {
            return 0.0;
        }
        
        double errorSum = 0.0;
        int size = Math.min(hardwareBer.size(), simulatedBer.size());
        
        for (int i = 0; i < size; i++) {
            // Absolute difference measured securely bypassing log constraints natively
            double diff = Math.abs(hardwareBer.get(i) - simulatedBer.get(i));
            errorSum += diff;
        }
        
        double averageError = errorSum / size;
        // Simple mapping structure scaling average errors cleanly
        double match = Math.max(0.0, 100.0 - (averageError * 100.0));
        return Math.min(match, 100.0);
    }

    // Audits and safely captures a permanent database footprint reflecting users hardware calibrations natively  
    public SigmfImportRecord saveImportRecord(Long simulationId, SigmfMetadata metadata, double estimatedSnr, double matchPerc, Long userId) {
        SigmfImportRecord record = new SigmfImportRecord();
        record.setSimulationId(simulationId);
        record.setCenterFrequency(metadata.getCenterFrequency());
        record.setSampleRate(metadata.getSampleRate());
        record.setDataType(metadata.getDataType());
        record.setEstimatedSnr(estimatedSnr);
        record.setBerMatchPercentage(matchPerc);
        record.setHardwareDescription(metadata.getHardware());
        record.setUserId(userId);
        return sigmfImportRepository.save(record);
    }
}

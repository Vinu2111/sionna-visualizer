package com.sionnavisualizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sionnavisualizer.dto.ChannelModelRequest;
import com.sionnavisualizer.dto.ChannelModelResponse;
import com.sionnavisualizer.model.ChannelModelResult;
import com.sionnavisualizer.repository.ChannelModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ChannelModelService {

    @Autowired
    private ChannelModelRepository channelModelRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${python-bridge.url:http://localhost:8001/simulate/demo}")
    private String pythonBridgeUrl;

    // A predefined valid list of 3GPP models
    private static final List<String> VALID_MODELS = Arrays.asList(
        "CDL-A", "CDL-B", "CDL-C", "TDL-A", "TDL-B", "TDL-C"
    );

    // Guard mechanism ensuring exact matching models are routed securely
    public void validateChannelModel(String model) {
        if (!VALID_MODELS.contains(model)) {
            throw new IllegalArgumentException("Invalid Channel Model. Expected one of: " + VALID_MODELS);
        }
    }

    // Connects completely cleanly to the Python FastAPI route dedicated to models
    public ChannelModelResponse callPythonBridge(ChannelModelRequest request) {
        String url = pythonBridgeUrl + "/simulate/channel-model";
        return restTemplate.postForObject(url, request, ChannelModelResponse.class);
    }

    // Persists the response JSON blocks into database strings
    public ChannelModelResult saveResult(ChannelModelResponse response, Long userId) {
        try {
            ChannelModelResult record = new ChannelModelResult();
            record.setChannelModel(response.getChannelModel());
            record.setModulation(response.getModulation());
            
            // Reconstruct endpoints if SNR was passed dynamically
            List<Double> snrList = response.getSnrDbRange();
            if (snrList != null && !snrList.isEmpty()) {
                record.setSnrMin(snrList.get(0));
                record.setSnrMax(snrList.get(snrList.size() - 1));
            }
            
            // Write complex structures natively as JSON strings directly into Postgres TEXT block
            record.setBerValues(objectMapper.writeValueAsString(response.getBerValues()));
            record.setDelayProfile(objectMapper.writeValueAsString(response.getDelayProfile()));
            
            record.setSimulationTimeSeconds(response.getSimulationTimeSeconds());
            record.setUserId(userId);
            
            return channelModelRepository.save(record);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode/save channel result: " + e.getMessage());
        }
    }

    // Retrieves past records using custom explicit Spring query methods natively
    public List<ChannelModelResult> getHistory(Long userId) {
        return channelModelRepository.findByUserId(userId);
    }
}

package com.sionnavisualizer.controller;

import jakarta.validation.Valid;

import com.sionnavisualizer.dto.ChannelModelRequest;
import com.sionnavisualizer.dto.ChannelModelResponse;
import com.sionnavisualizer.service.ChannelModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulate")
public class ChannelModelController {

    @Autowired
    private ChannelModelService channelModelService;

    // Handles the proxy connection validating the frontend models prior to executing in python engines
    @PostMapping("/channel-model")
    public ResponseEntity<ChannelModelResponse> simulateChannelModel(@Valid @RequestBody ChannelModelRequest request) {
        // Enforce strict model checking securely isolating our service
        channelModelService.validateChannelModel(request.getChannelModel());
        
        // Execute heavy-lifting network communication blocking safely synchronously
        ChannelModelResponse response = channelModelService.callPythonBridge(request);
        
        // Mocking user ID arbitrarily for demo tracking requirements, ordinarily recovered by Spring Security Auth context
        Long userId = 1L; 
        
        // Saves execution cleanly tracking metrics for the research platform
        channelModelService.saveResult(response, userId);
        
        return ResponseEntity.ok(response);
    }
}

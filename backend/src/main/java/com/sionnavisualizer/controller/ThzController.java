package com.sionnavisualizer.controller;

import jakarta.validation.Valid;

import com.sionnavisualizer.dto.SaveScenarioRequest;
import com.sionnavisualizer.dto.ThzRequest;
import com.sionnavisualizer.dto.ThzResponse;
import com.sionnavisualizer.dto.ThzScenarioResponse;
import com.sionnavisualizer.service.ThzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thz")
public class ThzController {

    @Autowired
    private ThzService thzService;

    // We assume user 1L as mock user ID for now
    private final Long MOCK_USER_ID = 1L;

    /**
     * Calculates the full 6G THz link budget and molecular absorption spectrum.
     */
    @PostMapping("/calculate")
    public ResponseEntity<ThzResponse> calculate(@Valid @RequestBody ThzRequest request) {
        ThzResponse response = thzService.calculate(request, MOCK_USER_ID);
        return ResponseEntity.ok(response);
    }

    /**
     * Reusable atmospheric scenarios are great for demoing concepts to other researchers.
     * This endpoint lists all saved scenarios for this user.
     */
    @GetMapping("/scenarios")
    public ResponseEntity<List<ThzScenarioResponse>> getUserScenarios() {
        List<ThzScenarioResponse> scenarios = thzService.getUserScenarios(MOCK_USER_ID);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * Saves the slider configuration to a named scenario (e.g., "Delhi Monsoon").
     */
    @PostMapping("/scenarios")
    public ResponseEntity<ThzScenarioResponse> saveScenario(@Valid @RequestBody SaveScenarioRequest request) {
        ThzScenarioResponse response = thzService.saveScenario(request, MOCK_USER_ID);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a given scenario.
     */
    @DeleteMapping("/scenarios/{id}")
    public ResponseEntity<Void> deleteScenario(@PathVariable Long id) {
        thzService.deleteScenario(id, MOCK_USER_ID);
        return ResponseEntity.ok().build();
    }
}

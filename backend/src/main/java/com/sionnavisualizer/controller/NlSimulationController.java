package com.sionnavisualizer.controller;

import jakarta.validation.Valid;

import com.sionnavisualizer.dto.ParseQueryRequest;
import com.sionnavisualizer.dto.ParsedParamsResponse;
import com.sionnavisualizer.model.NlParseRecord;
import com.sionnavisualizer.service.NlSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nl-simulation")
public class NlSimulationController {

    @Autowired
    private NlSimulationService nlSimulationService;

    private final Long MOCK_USER_ID = 1L;

    /**
     * Main parse endpoint — receives a natural language query,
     * sends it to Claude AI, and returns extracted simulation parameters.
     */
    @PostMapping("/parse")
    public ResponseEntity<?> parseQuery(@Valid @RequestBody ParseQueryRequest request) {
        try {
            ParsedParamsResponse result = nlSimulationService.parseQuery(request.getQuery(), MOCK_USER_ID);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            // Validation errors (no keywords, too long, empty) — return 400 with message
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Claude API errors, network issues, JSON parse failures
            return ResponseEntity.internalServerError().body(
                Map.of("error", "AI parsing failed: " + e.getMessage())
            );
        }
    }

    /**
     * Returns the 10 most recent parse attempts by this user
     * so they can reload a previous query without retyping it.
     */
    @GetMapping("/history")
    public ResponseEntity<List<NlParseRecord>> getHistory() {
        return ResponseEntity.ok(nlSimulationService.getHistory(MOCK_USER_ID));
    }
}

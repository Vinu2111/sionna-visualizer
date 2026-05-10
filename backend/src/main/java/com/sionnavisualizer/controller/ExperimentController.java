package com.sionnavisualizer.controller;

import jakarta.validation.Valid;

import com.sionnavisualizer.dto.*;
import com.sionnavisualizer.service.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ExperimentController {

    @Autowired
    private ExperimentService experimentService;

    // ----- EXPERIMENT ENDPOINTS -----
    
    @GetMapping("/experiments")
    public ResponseEntity<List<ExperimentResponse>> getExperiments() {
        Long userId = 1L; // Mock user resolution logically natively
        return ResponseEntity.ok(experimentService.getUserExperiments(userId));
    }

    @PostMapping("/experiments")
    public ResponseEntity<ExperimentResponse> createExperiment(@Valid @RequestBody CreateExperimentRequest request) {
        Long userId = 1L;
        return ResponseEntity.ok(experimentService.createExperiment(request, userId));
    }

    // ----- SIMULATION TAGGING ENDPOINTS -----

    @PostMapping("/simulations/{id}/tags")
    public ResponseEntity<Void> addTag(@PathVariable Long id, @Valid @RequestBody AddTagRequest request) {
        Long userId = 1L;
        experimentService.addTag(id, request.getTag(), userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/simulations/{id}/tags/{tag}")
    public ResponseEntity<Void> removeTag(@PathVariable Long id, @PathVariable String tag) {
        Long userId = 1L;
        experimentService.removeTag(id, tag, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/simulations/bulk-tag")
    public ResponseEntity<Void> bulkTag(@Valid @RequestBody BulkTagRequest request) {
        Long userId = 1L;
        experimentService.bulkAddTags(request.getSimulationIds(), request.getTags(), userId);
        return ResponseEntity.ok().build();
    }

    // ----- SIMULATION METADATA ENDPOINTS -----

    @PutMapping("/simulations/{id}/note")
    public ResponseEntity<Void> updateNote(@PathVariable Long id, @Valid @RequestBody NoteUpdateRequest request) {
        Long userId = 1L;
        experimentService.updateNote(id, request.getNote(), userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/simulations/{id}/star")
    public ResponseEntity<Void> toggleStar(@PathVariable Long id) {
        Long userId = 1L;
        experimentService.toggleStar(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/simulations/{id}/experiment")
    public ResponseEntity<Void> assignToExperiment(@PathVariable Long id, @Valid @RequestBody Map<String, Long> request) {
        Long userId = 1L;
        experimentService.assignExperiment(id, request.get("experimentId"), userId);
        return ResponseEntity.ok().build();
    }

    // ----- SEARCH AND TAG ENDPOINTS -----

    @GetMapping("/simulations/search")
    public ResponseEntity<List<SimulationHistoryResponse>> searchSimulations(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long experimentId,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean starred) {
        
        SearchRequest req = new SearchRequest();
        req.setQuery(q);
        req.setExperimentId(experimentId);
        req.setTags(tags);
        req.setStarred(starred);

        Long userId = 1L;
        return ResponseEntity.ok(experimentService.searchSimulations(req, userId));
    }

    @GetMapping("/simulations/tags")
    public ResponseEntity<List<TagCountResponse>> getTags() {
        Long userId = 1L;
        return ResponseEntity.ok(experimentService.getAllTagsWithCounts(userId));
    }
}

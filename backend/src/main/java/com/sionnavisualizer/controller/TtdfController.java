package com.sionnavisualizer.controller;

import jakarta.validation.Valid;

import com.sionnavisualizer.dto.*;
import com.sionnavisualizer.service.TtdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ttdf")
public class TtdfController {

    @Autowired
    private TtdfService ttdfService;
    private final Long MOCK_USER_ID = 1L;

    // ----- PROJECTS -----

    @GetMapping("/project")
    public ResponseEntity<TtdfProjectResponse> getProject() {
        return ResponseEntity.ok(ttdfService.getOrCreateProject(MOCK_USER_ID));
    }

    @PostMapping("/project")
    public ResponseEntity<TtdfProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest req) {
        return ResponseEntity.ok(ttdfService.updateProject(req, MOCK_USER_ID));
    }

    @PutMapping("/project")
    public ResponseEntity<TtdfProjectResponse> updateProject(@Valid @RequestBody CreateProjectRequest req) {
        return ResponseEntity.ok(ttdfService.updateProject(req, MOCK_USER_ID));
    }

    @PutMapping("/project/trl")
    public ResponseEntity<Void> updateTrl(@Valid @RequestBody UpdateTrlRequest req) {
        ttdfService.updateTrl(MOCK_USER_ID, req.getTrlLevel());
        return ResponseEntity.ok().build();
    }

    // ----- MILESTONES -----

    @GetMapping("/milestones")
    public ResponseEntity<List<MilestoneResponse>> getMilestones() {
        return ResponseEntity.ok(ttdfService.getMilestones(MOCK_USER_ID));
    }

    @PostMapping("/milestones")
    public ResponseEntity<MilestoneResponse> createMilestone(@Valid @RequestBody CreateMilestoneRequest req) {
        return ResponseEntity.ok(ttdfService.createMilestone(req, MOCK_USER_ID));
    }

    @PutMapping("/milestones/{id}/status")
    public ResponseEntity<Void> updateMilestoneStatus(@PathVariable Long id, @Valid @RequestBody java.util.Map<String, String> statusBody) {
        ttdfService.updateMilestoneStatus(id, statusBody.get("status"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/milestones/{id}/link-simulation")
    public ResponseEntity<Void> linkSimulation(@PathVariable Long id, @Valid @RequestBody LinkSimulationRequest req) {
        ttdfService.linkSimulation(id, req.getSimulationId(), MOCK_USER_ID);
        return ResponseEntity.ok().build();
    }

    // ----- KPIS -----

    @PutMapping("/kpis/{id}/actual-value")
    public ResponseEntity<Void> updateKpiValue(@PathVariable Long id, @Valid @RequestBody UpdateKpiValueRequest req) {
        ttdfService.updateKpiActualValue(id, req.getActualValue());
        return ResponseEntity.ok().build();
    }

    // ----- REPORT -----

    @PostMapping("/report/generate")
    public ResponseEntity<byte[]> generateReport(@Valid @RequestBody ReportOptionsRequest req) {
        byte[] pdfBytes = ttdfService.generateReport(req, MOCK_USER_ID);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "TTDF_Progress_Report.pdf");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}

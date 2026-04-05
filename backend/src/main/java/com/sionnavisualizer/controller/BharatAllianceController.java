package com.sionnavisualizer.controller;

import jakarta.validation.Valid;

import com.sionnavisualizer.dto.*;
import com.sionnavisualizer.service.BharatAllianceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bharat-alliance")
public class BharatAllianceController {

    @Autowired
    private BharatAllianceService service;
    private final Long MOCK_USER_ID = 1L;

    // ----- ORGANIZATION -----

    @GetMapping("/organization")
    public ResponseEntity<AllianceOrganizationResponse> getOrg() {
        return ResponseEntity.ok(service.getOrCreateOrganization(MOCK_USER_ID));
    }

    @PostMapping("/organization")
    public ResponseEntity<AllianceOrganizationResponse> createOrg(@Valid @RequestBody SaveOrganizationRequest req) {
        return ResponseEntity.ok(service.saveOrganization(req, MOCK_USER_ID));
    }

    @PutMapping("/organization")
    public ResponseEntity<AllianceOrganizationResponse> updateOrg(@Valid @RequestBody SaveOrganizationRequest req) {
        return ResponseEntity.ok(service.saveOrganization(req, MOCK_USER_ID));
    }

    // ----- PoCs -----

    @GetMapping("/pocs")
    public ResponseEntity<List<PocResponse>> getMyPocs() {
        return ResponseEntity.ok(service.getMyPocs(MOCK_USER_ID));
    }

    @PostMapping("/pocs")
    public ResponseEntity<PocResponse> registerPoc(@Valid @RequestBody RegisterPocRequest req) {
        return ResponseEntity.ok(service.registerPoc(req, MOCK_USER_ID));
    }

    @GetMapping("/pocs/{id}")
    public ResponseEntity<PocDetailResponse> getPocDetail(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPocDetail(id));
    }

    @PutMapping("/pocs/{id}/trl")
    public ResponseEntity<Void> advanceTrl(@PathVariable Long id, @Valid @RequestBody Map<String, Object> body) {
        int newTrl = (Integer) body.get("newTrl");
        String evidence = (String) body.getOrDefault("evidenceDescription", "");
        service.advanceTrl(id, newTrl, evidence, MOCK_USER_ID);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/pocs/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody Map<String, String> body) {
        // Update status directly on  poc record
        return ResponseEntity.ok().build();
    }

    // ----- SIMULATIONS -----

    @PostMapping("/pocs/{id}/simulations")
    public ResponseEntity<Void> linkSimulation(@PathVariable Long id, @Valid @RequestBody Map<String, Object> body) {
        Long simId = Long.parseLong(body.get("simulationId").toString());
        int trlFor = (Integer) body.get("trlEvidenceFor");
        service.linkSimulation(id, simId, trlFor, MOCK_USER_ID);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pocs/{id}/simulations")
    public ResponseEntity<PocDetailResponse> getSimulations(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPocDetail(id));
    }

    // ----- KPIs -----

    @GetMapping("/pocs/{id}/kpis")
    public ResponseEntity<PocDetailResponse> getKpis(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPocDetail(id));
    }

    @PutMapping("/kpis/{id}/actual-value")
    public ResponseEntity<Void> updateKpiValue(@PathVariable Long id, @Valid @RequestBody UpdateKpiValueRequest req) {
        service.updateKpiValue(id, req.getActualValue());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/kpi-templates/{allianceTrack}")
    public ResponseEntity<List<KpiTemplateResponse>> getTemplates(@PathVariable String allianceTrack) {
        return ResponseEntity.ok(service.getKpiTemplates(allianceTrack));
    }

    // ----- QUARTERLY STATUS -----

    @GetMapping("/pocs/{id}/quarterly-status")
    public ResponseEntity<List<QuarterlyStatusResponse>> getQuarterlyStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPocDetail(id).getQuarterlyStatus());
    }

    @PutMapping("/pocs/{id}/quarterly-status")
    public ResponseEntity<Void> updateQuarterlyStatus(@PathVariable Long id, @Valid @RequestBody Map<String, Object> body) {
        String quarter = (String) body.get("quarter");
        Integer year = (Integer) body.get("year");
        String status = (String) body.get("status");
        service.updateQuarterlyStatus(id, quarter, year, status);
        return ResponseEntity.ok().build();
    }

    // ----- REPORT -----

    @PostMapping("/report/generate")
    public ResponseEntity<byte[]> generateReport(@Valid @RequestBody AllianceReportRequest req) {
        byte[] data = service.generateReport(req, MOCK_USER_ID);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "BharatAlliance_Report.pdf");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}

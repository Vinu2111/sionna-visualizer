package com.sionnavisualizer.controller;

import jakarta.validation.Valid;

import com.sionnavisualizer.dto.LatexExportRequest;
import com.sionnavisualizer.dto.LatexExportResponse;
import com.sionnavisualizer.service.LatexExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
public class LatexExportController {

    @Autowired
    private LatexExportService latexExportService;

    // Triggers generation of a LaTeX standard parameter table representation
    @PostMapping("/latex")
    public ResponseEntity<LatexExportResponse> exportLatex(@Valid @RequestBody LatexExportRequest request) {
        
        // Pass off directly to isolated service
        LatexExportResponse response = latexExportService.processExport(request);
        
        // Fulfill 200 return envelope
        return ResponseEntity.ok(response);
    }
}

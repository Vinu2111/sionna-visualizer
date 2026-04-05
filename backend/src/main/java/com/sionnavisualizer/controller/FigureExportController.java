package com.sionnavisualizer.controller;

import jakarta.validation.Valid;

import com.sionnavisualizer.dto.FigureExportRequest;
import com.sionnavisualizer.dto.FigureExportResponse;
import com.sionnavisualizer.service.FigureExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
public class FigureExportController {

    @Autowired
    private FigureExportService figureExportService;

    // Endpoint to track figure exports for a given simulation
    @PostMapping("/figure")
    public ResponseEntity<FigureExportResponse> exportFigure(@Valid @RequestBody FigureExportRequest request) {
        // Process the incoming export request
        FigureExportResponse response = figureExportService.processExport(request);
        
        // Return OK 200 with the response data
        return ResponseEntity.ok(response);
    }
}

package com.sionnavisualizer.controller;

import jakarta.validation.Valid;

import com.sionnavisualizer.dto.ReproducibilityRequest;
import com.sionnavisualizer.service.ReproducibilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/export")
public class ReproducibilityController {

    @Autowired
    private ReproducibilityService reproducibilityService;

    // Orchestrates building of a standard file attachment utilizing HttpHeaders to force zip stream download
    @PostMapping("/reproducibility")
    public ResponseEntity<byte[]> getReproducibilityPackage(@Valid @RequestBody ReproducibilityRequest request) {
        try {
            // Triggers byte array construction in attached generic service layer
            byte[] zipBytes = reproducibilityService.generateReproducibilityPackage(request);

            // Setup application headers specific for forcing native downloads seamlessly
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("application/zip"));
            headers.setContentDispositionFormData("attachment", "reproducibility-package.zip");

            
            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            // Standard fallback block
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class FigureExportResponse {
    
    @NotNull
    
    @Min(0)
    private Long exportId;
    @NotBlank
    private String message;
    private LocalDateTime createdAt;
    
    public FigureExportResponse(Long exportId, String message, LocalDateTime createdAt) {
        this.exportId = exportId;
        this.message = message;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getExportId() { return exportId; }
    public void setExportId(Long exportId) { this.exportId = exportId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

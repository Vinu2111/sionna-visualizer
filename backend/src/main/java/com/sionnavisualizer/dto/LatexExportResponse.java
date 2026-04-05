package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class LatexExportResponse {

    @NotBlank

    private String latexContent;
    @NotNull
    @Min(0)
    private Long simulationId;
    private LocalDateTime generatedAt;

    public LatexExportResponse(String latexContent, Long simulationId, LocalDateTime generatedAt) {
        this.latexContent = latexContent;
        this.simulationId = simulationId;
        this.generatedAt = generatedAt;
    }

    // Standard Getters and Setters
    public String getLatexContent() { return latexContent; }
    public void setLatexContent(String latexContent) { this.latexContent = latexContent; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}

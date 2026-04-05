package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class AiExplanationResponse {
    @NotNull
    @Min(0)
    private Long anomalyId;
    @NotBlank
    private String fullExplanation;
    @NotBlank
    private String generatedAt;

    public Long getAnomalyId() { return anomalyId; }
    public void setAnomalyId(Long anomalyId) { this.anomalyId = anomalyId; }

    public String getFullExplanation() { return fullExplanation; }
    public void setFullExplanation(String fullExplanation) { this.fullExplanation = fullExplanation; }

    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
}

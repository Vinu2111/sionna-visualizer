package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class AnomalyRecordResponse {
    @NotNull
    @Min(0)
    private Long anomalyId;
    @NotBlank
    private String anomalyType;
    @NotBlank
    private String severity;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    @Min(0)
    private Double affectedSnrPoint;
    @NotNull
    @Min(0)
    private Double affectedBerValue;
    @NotBlank
    private String likelyCause;
    @NotBlank
    private String suggestedFix;
    @NotBlank
    private String aiExplanation;

    public Long getAnomalyId() { return anomalyId; }
    public void setAnomalyId(Long anomalyId) { this.anomalyId = anomalyId; }

    public String getAnomalyType() { return anomalyType; }
    public void setAnomalyType(String anomalyType) { this.anomalyType = anomalyType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getAffectedSnrPoint() { return affectedSnrPoint; }
    public void setAffectedSnrPoint(Double affectedSnrPoint) { this.affectedSnrPoint = affectedSnrPoint; }

    public Double getAffectedBerValue() { return affectedBerValue; }
    public void setAffectedBerValue(Double affectedBerValue) { this.affectedBerValue = affectedBerValue; }

    public String getLikelyCause() { return likelyCause; }
    public void setLikelyCause(String likelyCause) { this.likelyCause = likelyCause; }

    public String getSuggestedFix() { return suggestedFix; }
    public void setSuggestedFix(String suggestedFix) { this.suggestedFix = suggestedFix; }

    public String getAiExplanation() { return aiExplanation; }
    public void setAiExplanation(String aiExplanation) { this.aiExplanation = aiExplanation; }
}

package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class KpiTemplateResponse {
    @NotBlank
    private String kpiName;
    @NotBlank
    private String unit;
    @NotNull
    @Min(0)
    private Double suggestedTarget;
    @NotBlank
    private String allianceTrack;

    public KpiTemplateResponse(String kpiName, String unit, Double suggestedTarget, String allianceTrack) {
        this.kpiName = kpiName;
        this.unit = unit;
        this.suggestedTarget = suggestedTarget;
        this.allianceTrack = allianceTrack;
    }

    public String getKpiName() { return kpiName; }
    public void setKpiName(String kpiName) { this.kpiName = kpiName; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getSuggestedTarget() { return suggestedTarget; }
    public void setSuggestedTarget(Double suggestedTarget) { this.suggestedTarget = suggestedTarget; }

    public String getAllianceTrack() { return allianceTrack; }
    public void setAllianceTrack(String allianceTrack) { this.allianceTrack = allianceTrack; }
}

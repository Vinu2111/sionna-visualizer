package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class AllianceKpiResponse {
    @NotNull
    @Min(0)
    private Long kpiId;
    @NotBlank
    private String kpiName;
    @NotNull
    @Min(0)
    private Double targetValue;
    @NotNull
    @Min(0)
    private Double actualValue;
    @NotBlank
    private String unit;
    @NotBlank
    private String allianceTrack;
    @NotBlank
    private String status;

    public Long getKpiId() { return kpiId; }
    public void setKpiId(Long kpiId) { this.kpiId = kpiId; }

    public String getKpiName() { return kpiName; }
    public void setKpiName(String kpiName) { this.kpiName = kpiName; }

    public Double getTargetValue() { return targetValue; }
    public void setTargetValue(Double targetValue) { this.targetValue = targetValue; }

    public Double getActualValue() { return actualValue; }
    public void setActualValue(Double actualValue) { this.actualValue = actualValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getAllianceTrack() { return allianceTrack; }
    public void setAllianceTrack(String allianceTrack) { this.allianceTrack = allianceTrack; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

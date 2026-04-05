package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class RegisterPocRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String targetUseCase;
    @NotBlank
    private String allianceTrack;
    @NotNull
    @Min(0)
    private Integer currentTrl;
    @NotNull
    @Min(0)
    private Integer expectedCompletionTrl;
    private LocalDate targetCompletionDate;
    private List<KpiInput> kpis;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTargetUseCase() { return targetUseCase; }
    public void setTargetUseCase(String targetUseCase) { this.targetUseCase = targetUseCase; }

    public String getAllianceTrack() { return allianceTrack; }
    public void setAllianceTrack(String allianceTrack) { this.allianceTrack = allianceTrack; }

    public Integer getCurrentTrl() { return currentTrl; }
    public void setCurrentTrl(Integer currentTrl) { this.currentTrl = currentTrl; }

    public Integer getExpectedCompletionTrl() { return expectedCompletionTrl; }
    public void setExpectedCompletionTrl(Integer expectedCompletionTrl) { this.expectedCompletionTrl = expectedCompletionTrl; }

    public LocalDate getTargetCompletionDate() { return targetCompletionDate; }
    public void setTargetCompletionDate(LocalDate targetCompletionDate) { this.targetCompletionDate = targetCompletionDate; }

    public List<KpiInput> getKpis() { return kpis; }
    public void setKpis(List<KpiInput> kpis) { this.kpis = kpis; }

    public static class KpiInput {
        @NotBlank
        private String kpiName;
        @NotNull
        @Min(0)
    private Double targetValue;
        @NotBlank
        private String unit;

        public String getKpiName() { return kpiName; }
        public void setKpiName(String kpiName) { this.kpiName = kpiName; }

        public Double getTargetValue() { return targetValue; }
        public void setTargetValue(Double targetValue) { this.targetValue = targetValue; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }
}

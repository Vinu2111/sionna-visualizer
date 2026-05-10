package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.List;

public class AnomalyReportResponse {
    @NotNull
    @Min(0)
    private Long reportId;
    @NotNull
    @Min(0)
    private Long simulationId;
    @NotNull
    @Min(0)
    private Integer totalAnomalies;
    private Boolean hasCritical;
    @NotBlank
    private String overallStatus; // CLEAR / WARNING / CRITICAL
    @NotBlank
    private String analyzedAt;
    private List<AnomalyRecordResponse> anomalies;

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public Integer getTotalAnomalies() { return totalAnomalies; }
    public void setTotalAnomalies(Integer totalAnomalies) { this.totalAnomalies = totalAnomalies; }

    public Boolean getHasCritical() { return hasCritical; }
    public void setHasCritical(Boolean hasCritical) { this.hasCritical = hasCritical; }

    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }

    public String getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(String analyzedAt) { this.analyzedAt = analyzedAt; }

    public List<AnomalyRecordResponse> getAnomalies() { return anomalies; }
    public void setAnomalies(List<AnomalyRecordResponse> anomalies) { this.anomalies = anomalies; }
}

package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class MilestoneResponse {
    @NotNull
    @Min(0)
    private Long milestoneId;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    @Min(0)
    private Integer monthNumber;
    private LocalDate dueDate;
    @NotBlank
    private String status;
    @NotNull
    @Min(0)
    private Long linkedSimulationId;
    private List<KpiResponse> kpis;

    public Long getMilestoneId() { return milestoneId; }
    public void setMilestoneId(Long milestoneId) { this.milestoneId = milestoneId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMonthNumber() { return monthNumber; }
    public void setMonthNumber(Integer monthNumber) { this.monthNumber = monthNumber; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getLinkedSimulationId() { return linkedSimulationId; }
    public void setLinkedSimulationId(Long linkedSimulationId) { this.linkedSimulationId = linkedSimulationId; }

    public List<KpiResponse> getKpis() { return kpis; }
    public void setKpis(List<KpiResponse> kpis) { this.kpis = kpis; }

    public static class KpiResponse {
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
        private String metricType;
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

        public String getMetricType() { return metricType; }
        public void setMetricType(String metricType) { this.metricType = metricType; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}

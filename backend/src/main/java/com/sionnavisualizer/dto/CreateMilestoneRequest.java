package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class CreateMilestoneRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    @Min(0)
    private Integer monthNumber;
    private LocalDate dueDate;
    private List<KpiTargetDto> kpis;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMonthNumber() { return monthNumber; }
    public void setMonthNumber(Integer monthNumber) { this.monthNumber = monthNumber; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public List<KpiTargetDto> getKpis() { return kpis; }
    public void setKpis(List<KpiTargetDto> kpis) { this.kpis = kpis; }

    public static class KpiTargetDto {
        @NotBlank
        private String kpiName;
        @NotNull
        @Min(0)
    private Double targetValue;
        @NotBlank
        private String unit;
        @NotBlank
        private String metricType;

        public String getKpiName() { return kpiName; }
        public void setKpiName(String kpiName) { this.kpiName = kpiName; }

        public Double getTargetValue() { return targetValue; }
        public void setTargetValue(Double targetValue) { this.targetValue = targetValue; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }

        public String getMetricType() { return metricType; }
        public void setMetricType(String metricType) { this.metricType = metricType; }
    }
}

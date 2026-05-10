package com.sionnavisualizer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ttdf_kpi_targets")
public class TtdfKpiTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "milestone_id", nullable = false)
    private Long milestoneId;

    @Column(name = "kpi_name", length = 200)
    private String kpiName;

    @Column(name = "target_value")
    private Double targetValue;

    @Column(name = "actual_value")
    private Double actualValue;

    @Column(length = 50)
    private String unit;

    @Column(name = "metric_type", length = 20)
    private String metricType = "CUSTOM";

    @Column(length = 20)
    private String status = "PENDING";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMilestoneId() { return milestoneId; }
    public void setMilestoneId(Long milestoneId) { this.milestoneId = milestoneId; }

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

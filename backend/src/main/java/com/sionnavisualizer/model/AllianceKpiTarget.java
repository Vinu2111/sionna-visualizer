package com.sionnavisualizer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "alliance_kpi_targets")
public class AllianceKpiTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "poc_id", nullable = false)
    private Long pocId;

    @Column(name = "kpi_name", length = 200)
    private String kpiName;

    @Column(name = "target_value")
    private Double targetValue;

    @Column(name = "actual_value")
    private Double actualValue;

    @Column(length = 50)
    private String unit;

    @Column(name = "alliance_track", length = 100)
    private String allianceTrack;

    @Column(length = 20)
    private String status = "PENDING";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPocId() { return pocId; }
    public void setPocId(Long pocId) { this.pocId = pocId; }
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

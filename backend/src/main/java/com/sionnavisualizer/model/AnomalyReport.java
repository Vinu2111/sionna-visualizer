package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anomaly_reports")
public class AnomalyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simulation_id", nullable = false)
    private Long simulationId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "total_anomalies")
    private Integer totalAnomalies = 0;

    @Column(name = "has_critical")
    private Boolean hasCritical = false;

    // CLEAR = all physics checks passed
    // WARNING = medium/high severity issues found
    // CRITICAL = physically impossible results detected
    @Column(name = "overall_status", length = 20)
    private String overallStatus = "CLEAR";

    @Column(name = "analyzed_at", insertable = false, updatable = false)
    private LocalDateTime analyzedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getTotalAnomalies() { return totalAnomalies; }
    public void setTotalAnomalies(Integer totalAnomalies) { this.totalAnomalies = totalAnomalies; }
    public Boolean getHasCritical() { return hasCritical; }
    public void setHasCritical(Boolean hasCritical) { this.hasCritical = hasCritical; }
    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
}

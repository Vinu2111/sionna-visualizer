package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anomaly_records")
public class AnomalyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @Column(name = "simulation_id", nullable = false)
    private Long simulationId;

    @Column(name = "anomaly_type", length = 50)
    private String anomalyType;

    @Column(length = 20)
    private String severity;

    @Column(length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "affected_snr_point")
    private Double affectedSnrPoint;

    @Column(name = "affected_ber_value")
    private Double affectedBerValue;

    @Column(name = "likely_cause", length = 500)
    private String likelyCause;

    @Column(name = "suggested_fix", length = 500)
    private String suggestedFix;

    // Populated lazily when researcher clicks "Ask AI to Explain"
    @Column(name = "ai_explanation", columnDefinition = "TEXT")
    private String aiExplanation;

    @Column(name = "ai_explained_at")
    private LocalDateTime aiExplainedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }
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
    public LocalDateTime getAiExplainedAt() { return aiExplainedAt; }
    public void setAiExplainedAt(LocalDateTime aiExplainedAt) { this.aiExplainedAt = aiExplainedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

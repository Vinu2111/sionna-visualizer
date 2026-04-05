package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "poc_trl_history")
public class PocTrlHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "poc_id", nullable = false)
    private Long pocId;

    @Column(name = "trl_level", nullable = false)
    private Integer trlLevel;

    @Column(name = "achieved_at", insertable = false, updatable = false)
    private LocalDateTime achievedAt;

    @Column(name = "linked_simulation_id")
    private Long linkedSimulationId;

    @Column(name = "evidence_description", length = 1000)
    private String evidenceDescription;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPocId() { return pocId; }
    public void setPocId(Long pocId) { this.pocId = pocId; }
    public Integer getTrlLevel() { return trlLevel; }
    public void setTrlLevel(Integer trlLevel) { this.trlLevel = trlLevel; }
    public LocalDateTime getAchievedAt() { return achievedAt; }
    public void setAchievedAt(LocalDateTime achievedAt) { this.achievedAt = achievedAt; }
    public Long getLinkedSimulationId() { return linkedSimulationId; }
    public void setLinkedSimulationId(Long linkedSimulationId) { this.linkedSimulationId = linkedSimulationId; }
    public String getEvidenceDescription() { return evidenceDescription; }
    public void setEvidenceDescription(String evidenceDescription) { this.evidenceDescription = evidenceDescription; }
}

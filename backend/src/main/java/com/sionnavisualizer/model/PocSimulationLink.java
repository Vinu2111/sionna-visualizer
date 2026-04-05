package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "poc_simulation_links")
public class PocSimulationLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "poc_id", nullable = false)
    private Long pocId;

    @Column(name = "simulation_id", nullable = false)
    private Long simulationId;

    @Column(name = "trl_evidence_for")
    private Integer trlEvidenceFor;

    @Column(name = "linked_at", insertable = false, updatable = false)
    private LocalDateTime linkedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPocId() { return pocId; }
    public void setPocId(Long pocId) { this.pocId = pocId; }
    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }
    public Integer getTrlEvidenceFor() { return trlEvidenceFor; }
    public void setTrlEvidenceFor(Integer trlEvidenceFor) { this.trlEvidenceFor = trlEvidenceFor; }
    public LocalDateTime getLinkedAt() { return linkedAt; }
    public void setLinkedAt(LocalDateTime linkedAt) { this.linkedAt = linkedAt; }
}

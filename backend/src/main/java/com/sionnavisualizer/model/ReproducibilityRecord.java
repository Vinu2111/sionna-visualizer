package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reproducibility_packages")
public class ReproducibilityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simulation_id", nullable = false)
    private Long simulationId;

    @Column(name = "include_ber_data")
    private boolean includeBerData = true;

    @Column(name = "include_beam_data")
    private boolean includeBeamData = true;

    @Column(name = "anonymized")
    private boolean anonymized = false;

    @Column(name = "generated_at", insertable = false, updatable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();

    // Default constructor mandated by JPA spec
    public ReproducibilityRecord() {}

    // Getters and Setters mapping database to memory cleanly
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public boolean isIncludeBerData() { return includeBerData; }
    public void setIncludeBerData(boolean includeBerData) { this.includeBerData = includeBerData; }

    public boolean isIncludeBeamData() { return includeBeamData; }
    public void setIncludeBeamData(boolean includeBeamData) { this.includeBeamData = includeBeamData; }

    public boolean isAnonymized() { return anonymized; }
    public void setAnonymized(boolean anonymized) { this.anonymized = anonymized; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}

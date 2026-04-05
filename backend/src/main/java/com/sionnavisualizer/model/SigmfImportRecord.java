package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sigmf_imports")
public class SigmfImportRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simulation_id", nullable = false)
    private Long simulationId;

    @Column(name = "center_frequency")
    private Double centerFrequency;

    @Column(name = "sample_rate")
    private Double sampleRate;

    @Column(name = "data_type", length = 20)
    private String dataType;

    @Column(name = "estimated_snr")
    private Double estimatedSnr;

    @Column(name = "ber_match_percentage")
    private Double berMatchPercentage;

    @Column(name = "hardware_description", length = 500)
    private String hardwareDescription;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "imported_at", insertable = false, updatable = false)
    private LocalDateTime importedAt = LocalDateTime.now();

    public SigmfImportRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public Double getCenterFrequency() { return centerFrequency; }
    public void setCenterFrequency(Double centerFrequency) { this.centerFrequency = centerFrequency; }

    public Double getSampleRate() { return sampleRate; }
    public void setSampleRate(Double sampleRate) { this.sampleRate = sampleRate; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public Double getEstimatedSnr() { return estimatedSnr; }
    public void setEstimatedSnr(Double estimatedSnr) { this.estimatedSnr = estimatedSnr; }

    public Double getBerMatchPercentage() { return berMatchPercentage; }
    public void setBerMatchPercentage(Double berMatchPercentage) { this.berMatchPercentage = berMatchPercentage; }

    public String getHardwareDescription() { return hardwareDescription; }
    public void setHardwareDescription(String hardwareDescription) { this.hardwareDescription = hardwareDescription; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getImportedAt() { return importedAt; }
    public void setImportedAt(LocalDateTime importedAt) { this.importedAt = importedAt; }
}

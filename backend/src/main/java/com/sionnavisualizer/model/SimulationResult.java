package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity that maps to the "simulation_results" table in PostgreSQL.
 * Each row stores one completed OFDM simulation run.
 */
@Entity
@Table(name = "simulation_results")
public class SimulationResult {

    // Auto-generated primary key — PostgreSQL will assign a unique ID per row
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SNR values stored as a JSON string e.g. "[0.0, 2.0, 4.0, ...]"
    // We use String instead of a complex type to keep the database schema simple
    @Column(columnDefinition = "TEXT")
    private String snrDb;

    // BER values stored as a JSON string e.g. "[0.5, 0.3, 0.18, ...]"
    @Column(columnDefinition = "TEXT")
    private String ber;

    // Number of OFDM symbols used in this simulation run
    private Integer numOfdmSymbols;

    // FFT size (number of subcarriers) used in this simulation run
    private Integer fftSize;

    // Whether real NVIDIA Sionna GPU hardware was used, or mock data was generated
    private String hardwareUsed;

    // The timestamp returned by the Python bridge for when the simulation completed
    private LocalDateTime timestamp;

    // A securely tracked unique identifying public token dynamically assigned
    @Column(unique = true)
    private String shareToken;

    // Indicates whether this mathematical output securely authorizes unauthenticated public access natively
    @Column(nullable = false)
    private Boolean isPublic = true;

    // When this record was first saved to the database — set automatically before saving
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // This lifecycle callback runs automatically just before a new entity is saved
    // It ensures the createdAt field is always populated without manual setting
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---
    // These are required by JPA and Spring to read/write each field

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSnrDb() { return snrDb; }
    public void setSnrDb(String snrDb) { this.snrDb = snrDb; }

    public String getBer() { return ber; }
    public void setBer(String ber) { this.ber = ber; }

    public Integer getNumOfdmSymbols() { return numOfdmSymbols; }
    public void setNumOfdmSymbols(Integer numOfdmSymbols) { this.numOfdmSymbols = numOfdmSymbols; }

    public Integer getFftSize() { return fftSize; }
    public void setFftSize(Integer fftSize) { this.fftSize = fftSize; }

    public String getHardwareUsed() { return hardwareUsed; }
    public void setHardwareUsed(String hardwareUsed) { this.hardwareUsed = hardwareUsed; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getShareToken() { return shareToken; }
    public void setShareToken(String shareToken) { this.shareToken = shareToken; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

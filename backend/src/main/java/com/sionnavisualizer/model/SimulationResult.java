package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity mapped to the "simulation_results" table in PostgreSQL.
 * Each row stores one completed AWGN BER vs SNR simulation run.
 *
 * New columns added (Day 11):
 *   ber_theoretical   — JSON array string of the analytical BER curve
 *   ber_simulated     — JSON array string of the Monte-Carlo BER curve
 *   modulation_type   — e.g. "QPSK", "16QAM"
 *   code_rate         — fractional code rate, e.g. 0.50
 *   snr_min           — minimum SNR in dB used for this run
 *   snr_max           — maximum SNR in dB used for this run
 *   simulation_time_ms— wall-clock time for the Python simulation in ms
 *
 * Spring Boot ddl-auto=update will add any missing columns automatically.
 */
@Entity
@Table(name = "simulation_results")
public class SimulationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** SNR values stored as a JSON string, e.g. "[-5.0, -3.33, ..., 20.0]" */
    @Column(columnDefinition = "TEXT")
    private String snrDb;

    /**
     * Theoretical BER curve stored as a JSON string.
     * Computed analytically via closed-form Q/erfc formulas.
     */
    @Column(columnDefinition = "TEXT")
    private String berTheoretical;

    /**
     * Simulated BER curve stored as a JSON string.
     * Computed via Monte-Carlo AWGN noise injection in numpy.
     */
    @Column(columnDefinition = "TEXT")
    private String berSimulated;

    /** Modulation scheme used, e.g. "QPSK", "16QAM" */
    @Column(length = 20)
    private String modulationType;

    /** Fractional code rate, e.g. 0.50 */
    @Column(precision = 3, scale = 2)
    private BigDecimal codeRate;

    /** Minimum SNR in dB tested */
    @Column(precision = 5, scale = 2)
    private BigDecimal snrMin;

    /** Maximum SNR in dB tested */
    @Column(precision = 5, scale = 2)
    private BigDecimal snrMax;

    /** Wall-clock time the Python simulation took in milliseconds */
    private Integer simulationTimeMs;

    // ─── Legacy columns kept for backward compatibility ──────────────────────

    /** Legacy field — OFDM symbols per slot (from the old mock simulation) */
    private Integer numOfdmSymbols;

    /** Legacy field — FFT size from the old mock simulation */
    private Integer fftSize;

    /** Legacy field — whether real Sionna GPU was used, or mock data */
    private String hardwareUsed;

    /** The timestamp returned by the Python bridge */
    private LocalDateTime timestamp;

    // ─── Sharing / access control ────────────────────────────────────────────

    @Column(unique = true)
    private String shareToken;

    @Column(nullable = false)
    private Boolean isPublic = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSnrDb() { return snrDb; }
    public void setSnrDb(String snrDb) { this.snrDb = snrDb; }

    public String getBerTheoretical() { return berTheoretical; }
    public void setBerTheoretical(String berTheoretical) { this.berTheoretical = berTheoretical; }

    public String getBerSimulated() { return berSimulated; }
    public void setBerSimulated(String berSimulated) { this.berSimulated = berSimulated; }

    public String getModulationType() { return modulationType; }
    public void setModulationType(String modulationType) { this.modulationType = modulationType; }

    public BigDecimal getCodeRate() { return codeRate; }
    public void setCodeRate(BigDecimal codeRate) { this.codeRate = codeRate; }

    public BigDecimal getSnrMin() { return snrMin; }
    public void setSnrMin(BigDecimal snrMin) { this.snrMin = snrMin; }

    public BigDecimal getSnrMax() { return snrMax; }
    public void setSnrMax(BigDecimal snrMax) { this.snrMax = snrMax; }

    public Integer getSimulationTimeMs() { return simulationTimeMs; }
    public void setSimulationTimeMs(Integer simulationTimeMs) { this.simulationTimeMs = simulationTimeMs; }

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

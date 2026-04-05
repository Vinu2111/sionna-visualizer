package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "multi_simulator_comparisons")
public class MultiSimComparison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sionna_simulation_id", nullable = false)
    private Long sionnaSimulationId;

    @Column(name = "simulator_type", nullable = false, length = 50)
    private String simulatorType;

    // Use Postgres text mapping JSON dynamically allowing unbounded float points
    @Column(name = "snr_points", columnDefinition = "TEXT")
    private String snrPoints;

    @Column(name = "sionna_ber", columnDefinition = "TEXT")
    private String sionnaBer;

    @Column(name = "external_ber", columnDefinition = "TEXT")
    private String externalBer;

    @Column(name = "sionna_throughput", columnDefinition = "TEXT")
    private String sionnaThroughput;

    @Column(name = "external_throughput", columnDefinition = "TEXT")
    private String externalThroughput;

    @Column(name = "ber_crossover_snr")
    private Double berCrossoverSnr;

    @Column(name = "average_ber_difference")
    private Double averageBerDifference;

    @Column(name = "better_performer_at_20db", length = 50)
    private String betterPerformerAt20db;

    @Column(name = "matched_data_points")
    private Integer matchedDataPoints;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public MultiSimComparison() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSionnaSimulationId() { return sionnaSimulationId; }
    public void setSionnaSimulationId(Long sionnaSimulationId) { this.sionnaSimulationId = sionnaSimulationId; }

    public String getSimulatorType() { return simulatorType; }
    public void setSimulatorType(String simulatorType) { this.simulatorType = simulatorType; }

    public String getSnrPoints() { return snrPoints; }
    public void setSnrPoints(String snrPoints) { this.snrPoints = snrPoints; }

    public String getSionnaBer() { return sionnaBer; }
    public void setSionnaBer(String sionnaBer) { this.sionnaBer = sionnaBer; }

    public String getExternalBer() { return externalBer; }
    public void setExternalBer(String externalBer) { this.externalBer = externalBer; }

    public String getSionnaThroughput() { return sionnaThroughput; }
    public void setSionnaThroughput(String sionnaThroughput) { this.sionnaThroughput = sionnaThroughput; }

    public String getExternalThroughput() { return externalThroughput; }
    public void setExternalThroughput(String externalThroughput) { this.externalThroughput = externalThroughput; }

    public Double getBerCrossoverSnr() { return berCrossoverSnr; }
    public void setBerCrossoverSnr(Double berCrossoverSnr) { this.berCrossoverSnr = berCrossoverSnr; }

    public Double getAverageBerDifference() { return averageBerDifference; }
    public void setAverageBerDifference(Double averageBerDifference) { this.averageBerDifference = averageBerDifference; }

    public String getBetterPerformerAt20db() { return betterPerformerAt20db; }
    public void setBetterPerformerAt20db(String betterPerformerAt20db) { this.betterPerformerAt20db = betterPerformerAt20db; }

    public Integer getMatchedDataPoints() { return matchedDataPoints; }
    public void setMatchedDataPoints(Integer matchedDataPoints) { this.matchedDataPoints = matchedDataPoints; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "channel_model_simulations")
public class ChannelModelResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel_model", nullable = false, length = 10)
    private String channelModel;

    @Column(name = "modulation", nullable = false, length = 10)
    private String modulation;

    @Column(name = "snr_min")
    private double snrMin;

    @Column(name = "snr_max")
    private double snrMax;

    @Column(name = "carrier_frequency")
    private double carrierFrequency;

    @Column(name = "delay_spread")
    private double delaySpread;

    @Column(name = "ber_values", columnDefinition = "TEXT")
    private String berValues;

    @Column(name = "delay_profile", columnDefinition = "TEXT")
    private String delayProfile;

    @Column(name = "simulation_time_seconds")
    private double simulationTimeSeconds;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ChannelModelResult() {}

    // Clean generated boilerplate getters/setters linking standard java field semantics
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getChannelModel() { return channelModel; }
    public void setChannelModel(String channelModel) { this.channelModel = channelModel; }

    public String getModulation() { return modulation; }
    public void setModulation(String modulation) { this.modulation = modulation; }

    public double getSnrMin() { return snrMin; }
    public void setSnrMin(double snrMin) { this.snrMin = snrMin; }

    public double getSnrMax() { return snrMax; }
    public void setSnrMax(double snrMax) { this.snrMax = snrMax; }

    public double getCarrierFrequency() { return carrierFrequency; }
    public void setCarrierFrequency(double carrierFrequency) { this.carrierFrequency = carrierFrequency; }

    public double getDelaySpread() { return delaySpread; }
    public void setDelaySpread(double delaySpread) { this.delaySpread = delaySpread; }

    public String getBerValues() { return berValues; }
    public void setBerValues(String berValues) { this.berValues = berValues; }

    public String getDelayProfile() { return delayProfile; }
    public void setDelayProfile(String delayProfile) { this.delayProfile = delayProfile; }

    public double getSimulationTimeSeconds() { return simulationTimeSeconds; }
    public void setSimulationTimeSeconds(double simulationTimeSeconds) { this.simulationTimeSeconds = simulationTimeSeconds; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

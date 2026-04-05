package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "thz_scenarios")
public class ThzScenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who saved this scenario
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Friendly name like "Mumbai Monsoon"
    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "frequency_ghz")
    private Double frequencyGhz;

    @Column(name = "humidity_percent")
    private Double humidityPercent;

    @Column(name = "temperature_celsius")
    private Double temperatureCelsius;

    @Column(name = "pressure_hpa")
    private Double pressureHpa;

    @Column(name = "rain_rate_mm_per_hr")
    private Double rainRateMmPerHr;

    @Column(name = "link_distance_meters")
    private Double linkDistanceMeters;

    @Column(name = "tx_power_dbm")
    private Double txPowerDbm;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getFrequencyGhz() { return frequencyGhz; }
    public void setFrequencyGhz(Double frequencyGhz) { this.frequencyGhz = frequencyGhz; }

    public Double getHumidityPercent() { return humidityPercent; }
    public void setHumidityPercent(Double humidityPercent) { this.humidityPercent = humidityPercent; }

    public Double getTemperatureCelsius() { return temperatureCelsius; }
    public void setTemperatureCelsius(Double temperatureCelsius) { this.temperatureCelsius = temperatureCelsius; }

    public Double getPressureHpa() { return pressureHpa; }
    public void setPressureHpa(Double pressureHpa) { this.pressureHpa = pressureHpa; }

    public Double getRainRateMmPerHr() { return rainRateMmPerHr; }
    public void setRainRateMmPerHr(Double rainRateMmPerHr) { this.rainRateMmPerHr = rainRateMmPerHr; }

    public Double getLinkDistanceMeters() { return linkDistanceMeters; }
    public void setLinkDistanceMeters(Double linkDistanceMeters) { this.linkDistanceMeters = linkDistanceMeters; }

    public Double getTxPowerDbm() { return txPowerDbm; }
    public void setTxPowerDbm(Double txPowerDbm) { this.txPowerDbm = txPowerDbm; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

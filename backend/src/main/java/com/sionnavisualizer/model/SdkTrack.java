package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sdk_tracks")
public class SdkTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_key_id")
    private Long apiKeyId;

    @Column(name = "simulation_id")
    private Long simulationId;

    @Column(name = "sdk_version")
    private String sdkVersion;

    @Column(name = "sdk_language")
    private String sdkLanguage;

    @Column(name = "simulation_type")
    private String simulationType;

    @Column(name = "title", length = 300)
    private String title;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    @Column(name = "tracked_at")
    private LocalDateTime trackedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getApiKeyId() { return apiKeyId; }
    public void setApiKeyId(Long apiKeyId) { this.apiKeyId = apiKeyId; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public String getSdkVersion() { return sdkVersion; }
    public void setSdkVersion(String sdkVersion) { this.sdkVersion = sdkVersion; }

    public String getSdkLanguage() { return sdkLanguage; }
    public void setSdkLanguage(String sdkLanguage) { this.sdkLanguage = sdkLanguage; }

    public String getSimulationType() { return simulationType; }
    public void setSimulationType(String simulationType) { this.simulationType = simulationType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public LocalDateTime getTrackedAt() { return trackedAt; }
    public void setTrackedAt(LocalDateTime trackedAt) { this.trackedAt = trackedAt; }
}

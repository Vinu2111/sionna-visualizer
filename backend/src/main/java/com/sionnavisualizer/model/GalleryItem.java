package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gallery_items")
public class GalleryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simulation_id", nullable = false)
    private Long simulationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(length = 20)
    private String visibility = "PUBLIC";

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "fork_count")
    private Long forkCount = 0L;

    @Column(name = "download_count")
    private Long downloadCount = 0L;

    @Column(name = "custom_tags", columnDefinition = "TEXT")
    private String customTags;

    @Column(name = "published_at", insertable = false, updatable = false)
    private LocalDateTime publishedAt = LocalDateTime.now();

    public GalleryItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }

    public Long getForkCount() { return forkCount; }
    public void setForkCount(Long forkCount) { this.forkCount = forkCount; }

    public Long getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Long downloadCount) { this.downloadCount = downloadCount; }

    public String getCustomTags() { return customTags; }
    public void setCustomTags(String customTags) { this.customTags = customTags; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}

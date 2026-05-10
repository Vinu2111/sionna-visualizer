package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class GalleryItemResponse {
    @NotNull
    @Min(0)
    private Long galleryId;
    @NotNull
    @Min(0)
    private Long simulationId;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String authorName;
    @NotBlank
    private String authorInitials;
    @NotBlank
    private String channelModel;
    @NotBlank
    private String modulation;
    @NotNull
    @Min(0)
    private double frequencyGhz;
    private List<Double> berValues;
    private List<Double> snrRange;
    @NotNull
    @Min(0)
    private Long viewCount;
    @NotNull
    @Min(0)
    private Long forkCount;
    @NotNull
    @Min(0)
    private Long downloadCount;
    private List<String> tags;
    @NotBlank
    private String publishedAt;
    @NotBlank
    private String visibility;
    
    // Specifically mapped for explicit detail requests smoothly
    private Object simulationParameters;
    private Object fullBerData;
    private Object comments;

    public Long getGalleryId() { return galleryId; }
    public void setGalleryId(Long galleryId) { this.galleryId = galleryId; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorInitials() { return authorInitials; }
    public void setAuthorInitials(String authorInitials) { this.authorInitials = authorInitials; }

    public String getChannelModel() { return channelModel; }
    public void setChannelModel(String channelModel) { this.channelModel = channelModel; }

    public String getModulation() { return modulation; }
    public void setModulation(String modulation) { this.modulation = modulation; }

    public double getFrequencyGhz() { return frequencyGhz; }
    public void setFrequencyGhz(double frequencyGhz) { this.frequencyGhz = frequencyGhz; }

    public List<Double> getBerValues() { return berValues; }
    public void setBerValues(List<Double> berValues) { this.berValues = berValues; }

    public List<Double> getSnrRange() { return snrRange; }
    public void setSnrRange(List<Double> snrRange) { this.snrRange = snrRange; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }

    public Long getForkCount() { return forkCount; }
    public void setForkCount(Long forkCount) { this.forkCount = forkCount; }

    public Long getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Long downloadCount) { this.downloadCount = downloadCount; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public Object getSimulationParameters() { return simulationParameters; }
    public void setSimulationParameters(Object simulationParameters) { this.simulationParameters = simulationParameters; }

    public Object getFullBerData() { return fullBerData; }
    public void setFullBerData(Object fullBerData) { this.fullBerData = fullBerData; }

    public Object getComments() { return comments; }
    public void setComments(Object comments) { this.comments = comments; }
}

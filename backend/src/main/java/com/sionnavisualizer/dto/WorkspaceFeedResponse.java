package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class WorkspaceFeedResponse {
    @NotNull
    @Min(0)
    private Long simulationId;
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
    private Double frequencyGhz;
    @NotNull
    @Min(0)
    private Double berAt20db;
    @NotNull
    @Min(0)
    private Integer antennas;
    @NotNull
    @Min(0)
    private Long commentCount;
    @NotNull
    @Min(0)
    private Long annotationCount;
    @NotBlank
    private String createdAt;

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorInitials() { return authorInitials; }
    public void setAuthorInitials(String authorInitials) { this.authorInitials = authorInitials; }

    public String getChannelModel() { return channelModel; }
    public void setChannelModel(String channelModel) { this.channelModel = channelModel; }

    public String getModulation() { return modulation; }
    public void setModulation(String modulation) { this.modulation = modulation; }

    public Double getFrequencyGhz() { return frequencyGhz; }
    public void setFrequencyGhz(Double frequencyGhz) { this.frequencyGhz = frequencyGhz; }

    public Double getBerAt20db() { return berAt20db; }
    public void setBerAt20db(Double berAt20db) { this.berAt20db = berAt20db; }

    public Integer getAntennas() { return antennas; }
    public void setAntennas(Integer antennas) { this.antennas = antennas; }

    public Long getCommentCount() { return commentCount; }
    public void setCommentCount(Long commentCount) { this.commentCount = commentCount; }

    public Long getAnnotationCount() { return annotationCount; }
    public void setAnnotationCount(Long annotationCount) { this.annotationCount = annotationCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

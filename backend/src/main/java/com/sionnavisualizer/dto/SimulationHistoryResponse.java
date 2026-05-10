package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.List;

public class SimulationHistoryResponse {
    @NotNull
    @Min(0)
    private Long simulationId;
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
    private Long simulationTimeSeconds;
    private List<String> tags;
    @NotBlank
    private String note;
    private Boolean starred;
    @NotNull
    @Min(0)
    private Long experimentId;
    @NotBlank
    private String experimentName;
    @NotBlank
    private String experimentColor;
    @NotBlank
    private String createdAt;

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public String getChannelModel() { return channelModel; }
    public void setChannelModel(String channelModel) { this.channelModel = channelModel; }

    public String getModulation() { return modulation; }
    public void setModulation(String modulation) { this.modulation = modulation; }

    public Double getFrequencyGhz() { return frequencyGhz; }
    public void setFrequencyGhz(Double frequencyGhz) { this.frequencyGhz = frequencyGhz; }

    public Double getBerAt20db() { return berAt20db; }
    public void setBerAt20db(Double berAt20db) { this.berAt20db = berAt20db; }

    public Long getSimulationTimeSeconds() { return simulationTimeSeconds; }
    public void setSimulationTimeSeconds(Long simulationTimeSeconds) { this.simulationTimeSeconds = simulationTimeSeconds; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Boolean getStarred() { return starred; }
    public void setStarred(Boolean starred) { this.starred = starred; }

    public Long getExperimentId() { return experimentId; }
    public void setExperimentId(Long experimentId) { this.experimentId = experimentId; }

    public String getExperimentName() { return experimentName; }
    public void setExperimentName(String experimentName) { this.experimentName = experimentName; }

    public String getExperimentColor() { return experimentColor; }
    public void setExperimentColor(String experimentColor) { this.experimentColor = experimentColor; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

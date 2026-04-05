package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class ExperimentResponse {
    @NotNull
    @Min(0)
    private Long experimentId;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private String color;
    @NotNull
    @Min(0)
    private Long simulationCount;
    @NotBlank
    private String createdAt;

    public Long getExperimentId() { return experimentId; }
    public void setExperimentId(Long experimentId) { this.experimentId = experimentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Long getSimulationCount() { return simulationCount; }
    public void setSimulationCount(Long simulationCount) { this.simulationCount = simulationCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

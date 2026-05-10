package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class ThzScenarioResponse {
    @NotNull
    @Min(0)
    private Long scenarioId;
    @NotBlank
    private String name;
    private ThzRequest params;
    @NotBlank
    private String createdAt;

    public Long getScenarioId() { return scenarioId; }
    public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ThzRequest getParams() { return params; }
    public void setParams(ThzRequest params) { this.params = params; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

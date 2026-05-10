package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

public class LinkSimulationRequest {
    @NotNull
    @Min(0)
    private Long simulationId;

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }
}

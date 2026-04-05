package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class LatexExportRequest {

    @NotNull

    @Min(0)
    private Long simulationId;
    @NotBlank
    private String tableCaption;
    @NotBlank
    private String tableLabel;

    // Standard Getters and Setters required for JSON mapping
    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public String getTableCaption() { return tableCaption; }
    public void setTableCaption(String tableCaption) { this.tableCaption = tableCaption; }

    public String getTableLabel() { return tableLabel; }
    public void setTableLabel(String tableLabel) { this.tableLabel = tableLabel; }
}

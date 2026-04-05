package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class FigureExportRequest {
    
    @NotNull
    
    @Min(0)
    private Long simulationId;
    @NotBlank
    private String journalStyle; // "IEEE" or "NATURE"
    @NotBlank
    private String exportFormat; // "SVG" or "PDF"
    @NotBlank
    private String chartType;
    
    // Getters and Setters
    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public String getJournalStyle() { return journalStyle; }
    public void setJournalStyle(String journalStyle) { this.journalStyle = journalStyle; }

    public String getExportFormat() { return exportFormat; }
    public void setExportFormat(String exportFormat) { this.exportFormat = exportFormat; }

    public String getChartType() { return chartType; }
    public void setChartType(String chartType) { this.chartType = chartType; }
}

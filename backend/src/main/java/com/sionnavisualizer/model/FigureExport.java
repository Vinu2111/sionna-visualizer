package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "figure_exports")
public class FigureExport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simulation_id", nullable = false)
    private Long simulationId;

    @Column(name = "journal_style", nullable = false, length = 20)
    private String journalStyle;

    @Column(name = "export_format", nullable = false, length = 10)
    private String exportFormat;

    @Column(name = "chart_type", length = 50)
    private String chartType;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default constructor
    public FigureExport() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public String getJournalStyle() { return journalStyle; }
    public void setJournalStyle(String journalStyle) { this.journalStyle = journalStyle; }

    public String getExportFormat() { return exportFormat; }
    public void setExportFormat(String exportFormat) { this.exportFormat = exportFormat; }

    public String getChartType() { return chartType; }
    public void setChartType(String chartType) { this.chartType = chartType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

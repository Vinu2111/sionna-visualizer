package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "latex_exports")
public class LatexExportRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simulation_id", nullable = false)
    private Long simulationId;

    @Column(name = "table_caption", length = 200)
    private String tableCaption;

    @Column(name = "table_label", length = 100)
    private String tableLabel;

    @Column(name = "generated_at", insertable = false, updatable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();

    // Default constructor for JPA
    public LatexExportRecord() {}

    // Clean, standard Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public String getTableCaption() { return tableCaption; }
    public void setTableCaption(String tableCaption) { this.tableCaption = tableCaption; }

    public String getTableLabel() { return tableLabel; }
    public void setTableLabel(String tableLabel) { this.tableLabel = tableLabel; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}

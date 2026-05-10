package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulation_versions")
public class SimulationVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simulation_id", nullable = false)
    private Long simulationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_by_name", length = 100)
    private String createdByName;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "parameters_snapshot", columnDefinition = "TEXT", nullable = false)
    private String parametersSnapshot;

    @Column(name = "changed_fields", columnDefinition = "TEXT")
    private String changedFields;

    @Column(name = "is_restore")
    private Boolean isRestore = false;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

    public String getParametersSnapshot() { return parametersSnapshot; }
    public void setParametersSnapshot(String parametersSnapshot) { this.parametersSnapshot = parametersSnapshot; }

    public String getChangedFields() { return changedFields; }
    public void setChangedFields(String changedFields) { this.changedFields = changedFields; }

    public Boolean getIsRestore() { return isRestore; }
    public void setIsRestore(Boolean isRestore) { this.isRestore = isRestore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

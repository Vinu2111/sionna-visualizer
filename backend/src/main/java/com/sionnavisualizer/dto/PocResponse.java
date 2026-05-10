package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class PocResponse {
    @NotNull
    @Min(0)
    private Long pocId;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String targetUseCase;
    @NotBlank
    private String allianceTrack;
    @NotNull
    @Min(0)
    private Integer currentTrl;
    @NotNull
    @Min(0)
    private Integer expectedCompletionTrl;
    @NotBlank
    private String status;
    @NotBlank
    private String targetCompletionDate;
    @NotNull
    @Min(0)
    private Long linkedSimulationCount;
    @NotBlank
    private String updatedAt;

    public Long getPocId() { return pocId; }
    public void setPocId(Long pocId) { this.pocId = pocId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTargetUseCase() { return targetUseCase; }
    public void setTargetUseCase(String targetUseCase) { this.targetUseCase = targetUseCase; }

    public String getAllianceTrack() { return allianceTrack; }
    public void setAllianceTrack(String allianceTrack) { this.allianceTrack = allianceTrack; }

    public Integer getCurrentTrl() { return currentTrl; }
    public void setCurrentTrl(Integer currentTrl) { this.currentTrl = currentTrl; }

    public Integer getExpectedCompletionTrl() { return expectedCompletionTrl; }
    public void setExpectedCompletionTrl(Integer expectedCompletionTrl) { this.expectedCompletionTrl = expectedCompletionTrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTargetCompletionDate() { return targetCompletionDate; }
    public void setTargetCompletionDate(String targetCompletionDate) { this.targetCompletionDate = targetCompletionDate; }

    public Long getLinkedSimulationCount() { return linkedSimulationCount; }
    public void setLinkedSimulationCount(Long linkedSimulationCount) { this.linkedSimulationCount = linkedSimulationCount; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}

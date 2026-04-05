package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bharat_pocs")
public class BharatPoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "org_id")
    private Long orgId;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_use_case", length = 500)
    private String targetUseCase;

    @Column(name = "alliance_track", length = 100)
    private String allianceTrack;

    @Column(name = "current_trl")
    private Integer currentTrl = 1;

    @Column(name = "expected_completion_trl")
    private Integer expectedCompletionTrl;

    @Column(length = 30)
    private String status = "ACTIVE";

    @Column(name = "target_completion_date")
    private LocalDate targetCompletionDate;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
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
    public LocalDate getTargetCompletionDate() { return targetCompletionDate; }
    public void setTargetCompletionDate(LocalDate targetCompletionDate) { this.targetCompletionDate = targetCompletionDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

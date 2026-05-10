package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ttdf_projects")
public class TtdfProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(length = 300)
    private String title;

    @Column(name = "ttdf_grant_id", length = 100)
    private String ttdfGrantId;

    @Column(name = "pi_name", length = 200)
    private String piName;

    @Column(length = 300)
    private String institution;

    @Column(name = "grant_amount_lakhs")
    private Double grantAmountLakhs;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "current_trl")
    private Integer currentTrl = 1;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTtdfGrantId() { return ttdfGrantId; }
    public void setTtdfGrantId(String ttdfGrantId) { this.ttdfGrantId = ttdfGrantId; }

    public String getPiName() { return piName; }
    public void setPiName(String piName) { this.piName = piName; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public Double getGrantAmountLakhs() { return grantAmountLakhs; }
    public void setGrantAmountLakhs(Double grantAmountLakhs) { this.grantAmountLakhs = grantAmountLakhs; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getCurrentTrl() { return currentTrl; }
    public void setCurrentTrl(Integer currentTrl) { this.currentTrl = currentTrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

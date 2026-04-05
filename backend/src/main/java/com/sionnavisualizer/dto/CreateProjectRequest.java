package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateProjectRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String ttdfGrantId;
    @NotBlank
    private String piName;
    @NotBlank
    private String institution;
    @NotNull
    @Min(0)
    private Double grantAmountLakhs;
    private LocalDate startDate;
    private LocalDate endDate;

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
}

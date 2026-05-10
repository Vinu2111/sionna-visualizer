package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class QuarterlyStatusResponse {
    @NotNull
    @Min(0)
    private Long id;
    @NotBlank
    private String quarter;
    @NotNull
    @Min(0)
    private Integer year;
    @NotBlank
    private String status;
    @NotBlank
    private String dueDate;
    @NotBlank
    private String submittedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuarter() { return quarter; }
    public void setQuarter(String quarter) { this.quarter = quarter; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
}

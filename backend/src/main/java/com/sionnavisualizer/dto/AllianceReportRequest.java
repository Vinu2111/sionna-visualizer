package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class AllianceReportRequest {
    @NotBlank
    private String reportType;
    @NotNull
    @Min(0)
    private Long pocId;
    @NotBlank
    private String quarter;
    @NotNull
    @Min(0)
    private Integer year;

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public Long getPocId() { return pocId; }
    public void setPocId(Long pocId) { this.pocId = pocId; }

    public String getQuarter() { return quarter; }
    public void setQuarter(String quarter) { this.quarter = quarter; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}

package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class ReportOptionsRequest {
    @NotBlank
    private String reportType;
    private LocalDate fromDate;
    private LocalDate toDate;
    private List<String> includeSections;

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public List<String> getIncludeSections() { return includeSections; }
    public void setIncludeSections(List<String> includeSections) { this.includeSections = includeSections; }
}

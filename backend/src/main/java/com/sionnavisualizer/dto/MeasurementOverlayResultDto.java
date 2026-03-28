package com.sionnavisualizer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MeasurementOverlayResultDto {

    @JsonProperty("comparison_points")
    private List<ComparisonPointDto> comparisonPoints;

    @JsonProperty("calibration_summary")
    private CalibrationSummaryDto calibrationSummary;

    @JsonProperty("simulation_type")
    private String simulationType;

    @JsonProperty("performance")
    private PerformanceDto performance;

    public List<ComparisonPointDto> getComparisonPoints() { return comparisonPoints; }
    public void setComparisonPoints(List<ComparisonPointDto> v) { this.comparisonPoints = v; }

    public CalibrationSummaryDto getCalibrationSummary() { return calibrationSummary; }
    public void setCalibrationSummary(CalibrationSummaryDto v) { this.calibrationSummary = v; }

    public String getSimulationType() { return simulationType; }
    public void setSimulationType(String v) { this.simulationType = v; }

    public PerformanceDto getPerformance() { return performance; }
    public void setPerformance(PerformanceDto v) { this.performance = v; }
}

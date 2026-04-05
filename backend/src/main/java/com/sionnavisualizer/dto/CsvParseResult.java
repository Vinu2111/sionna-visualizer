package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CsvParseResult {
    @NotBlank
    private String simulatorType;
    private List<Double> snrValues;
    private List<Double> berValues;
    private List<Double> throughputValues;
    private List<String> detectedColumns;
    private boolean hasThroughput;

    public String getSimulatorType() { return simulatorType; }
    public void setSimulatorType(String simulatorType) { this.simulatorType = simulatorType; }

    public List<Double> getSnrValues() { return snrValues; }
    public void setSnrValues(List<Double> snrValues) { this.snrValues = snrValues; }

    public List<Double> getBerValues() { return berValues; }
    public void setBerValues(List<Double> berValues) { this.berValues = berValues; }

    public List<Double> getThroughputValues() { return throughputValues; }
    public void setThroughputValues(List<Double> throughputValues) { this.throughputValues = throughputValues; }

    public List<String> getDetectedColumns() { return detectedColumns; }
    public void setDetectedColumns(List<String> detectedColumns) { this.detectedColumns = detectedColumns; }

    public boolean isHasThroughput() { return hasThroughput; }
    public void setHasThroughput(boolean hasThroughput) { this.hasThroughput = hasThroughput; }
}

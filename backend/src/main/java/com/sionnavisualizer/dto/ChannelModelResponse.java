package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public class ChannelModelResponse {
    @NotBlank
    private String channelModel;
    @NotBlank
    private String modulation;
    private List<Double> snrDbRange;
    private List<Double> berValues;
    private List<Double> theoreticalBer;
    private List<Map<String, Double>> delayProfile;
    @NotNull
    @Min(0)
    private double simulationTimeSeconds;
    @NotNull
    @Min(0)
    private int numPaths;

    // Normal standard getter & setters mappings for Jackson JSON extraction
    public String getChannelModel() { return channelModel; }
    public void setChannelModel(String channelModel) { this.channelModel = channelModel; }

    public String getModulation() { return modulation; }
    public void setModulation(String modulation) { this.modulation = modulation; }

    public List<Double> getSnrDbRange() { return snrDbRange; }
    public void setSnrDbRange(List<Double> snrDbRange) { this.snrDbRange = snrDbRange; }

    public List<Double> getBerValues() { return berValues; }
    public void setBerValues(List<Double> berValues) { this.berValues = berValues; }

    public List<Double> getTheoreticalBer() { return theoreticalBer; }
    public void setTheoreticalBer(List<Double> theoreticalBer) { this.theoreticalBer = theoreticalBer; }

    public List<Map<String, Double>> getDelayProfile() { return delayProfile; }
    public void setDelayProfile(List<Map<String, Double>> delayProfile) { this.delayProfile = delayProfile; }

    public double getSimulationTimeSeconds() { return simulationTimeSeconds; }
    public void setSimulationTimeSeconds(double simulationTimeSeconds) { this.simulationTimeSeconds = simulationTimeSeconds; }

    public int getNumPaths() { return numPaths; }
    public void setNumPaths(int numPaths) { this.numPaths = numPaths; }
}

package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ComparisonResponse {
    @NotNull
    @Min(0)
    private Long comparisonId;
    @NotNull
    @Min(0)
    private Long sionnaSimulationId;
    @NotBlank
    private String simulatorType;
    private List<Double> snrPoints;
    private List<Double> sionnaBer;
    private List<Double> externalBer;
    private List<Double> sionnaThroughput;
    private List<Double> externalThroughput;
    
    @NotNull
    
    @Min(0)
    private double berCrossoverSnr;
    @NotNull
    @Min(0)
    private double averageBerDifference;
    @NotBlank
    private String betterPerformerAt20db;
    @NotNull
    @Min(0)
    private int matchedDataPoints;
    @NotBlank
    private String createdAt;

    public Long getComparisonId() { return comparisonId; }
    public void setComparisonId(Long comparisonId) { this.comparisonId = comparisonId; }

    public Long getSionnaSimulationId() { return sionnaSimulationId; }
    public void setSionnaSimulationId(Long sionnaSimulationId) { this.sionnaSimulationId = sionnaSimulationId; }

    public String getSimulatorType() { return simulatorType; }
    public void setSimulatorType(String simulatorType) { this.simulatorType = simulatorType; }

    public List<Double> getSnrPoints() { return snrPoints; }
    public void setSnrPoints(List<Double> snrPoints) { this.snrPoints = snrPoints; }

    public List<Double> getSionnaBer() { return sionnaBer; }
    public void setSionnaBer(List<Double> sionnaBer) { this.sionnaBer = sionnaBer; }

    public List<Double> getExternalBer() { return externalBer; }
    public void setExternalBer(List<Double> externalBer) { this.externalBer = externalBer; }

    public List<Double> getSionnaThroughput() { return sionnaThroughput; }
    public void setSionnaThroughput(List<Double> sionnaThroughput) { this.sionnaThroughput = sionnaThroughput; }

    public List<Double> getExternalThroughput() { return externalThroughput; }
    public void setExternalThroughput(List<Double> externalThroughput) { this.externalThroughput = externalThroughput; }

    public double getBerCrossoverSnr() { return berCrossoverSnr; }
    public void setBerCrossoverSnr(double berCrossoverSnr) { this.berCrossoverSnr = berCrossoverSnr; }

    public double getAverageBerDifference() { return averageBerDifference; }
    public void setAverageBerDifference(double averageBerDifference) { this.averageBerDifference = averageBerDifference; }

    public String getBetterPerformerAt20db() { return betterPerformerAt20db; }
    public void setBetterPerformerAt20db(String betterPerformerAt20db) { this.betterPerformerAt20db = betterPerformerAt20db; }

    public int getMatchedDataPoints() { return matchedDataPoints; }
    public void setMatchedDataPoints(int matchedDataPoints) { this.matchedDataPoints = matchedDataPoints; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class ComparisonStats {
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

    public double getBerCrossoverSnr() { return berCrossoverSnr; }
    public void setBerCrossoverSnr(double berCrossoverSnr) { this.berCrossoverSnr = berCrossoverSnr; }

    public double getAverageBerDifference() { return averageBerDifference; }
    public void setAverageBerDifference(double averageBerDifference) { this.averageBerDifference = averageBerDifference; }

    public String getBetterPerformerAt20db() { return betterPerformerAt20db; }
    public void setBetterPerformerAt20db(String betterPerformerAt20db) { this.betterPerformerAt20db = betterPerformerAt20db; }

    public int getMatchedDataPoints() { return matchedDataPoints; }
    public void setMatchedDataPoints(int matchedDataPoints) { this.matchedDataPoints = matchedDataPoints; }
}

package com.sionnavisualizer.dto;

import java.util.List;

public class InterpolatedData {
    private List<Double> matchedSnrPoints;
    private List<Double> sionnaBer;
    private List<Double> externalBer;
    
    public List<Double> getMatchedSnrPoints() { return matchedSnrPoints; }
    public void setMatchedSnrPoints(List<Double> matchedSnrPoints) { this.matchedSnrPoints = matchedSnrPoints; }

    public List<Double> getSionnaBer() { return sionnaBer; }
    public void setSionnaBer(List<Double> sionnaBer) { this.sionnaBer = sionnaBer; }

    public List<Double> getExternalBer() { return externalBer; }
    public void setExternalBer(List<Double> externalBer) { this.externalBer = externalBer; }
}

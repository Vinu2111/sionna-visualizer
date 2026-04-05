package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class SigmfImportResponse {
    private SigmfMetadata metadata;
    @NotNull
    @Min(0)
    private double estimatedSnrDb;
    private List<Double> berEstimate;
    private List<Double> snrRange;
    private List<Double> iqSamplesI;
    private List<Double> iqSamplesQ;
    @NotNull
    @Min(0)
    private double berMatchPercentage;
    
    // Extracted simulation base to allow dashboard mapping securely directly automatically
    private List<Double> simulatedBer;

    public SigmfMetadata getMetadata() { return metadata; }
    public void setMetadata(SigmfMetadata metadata) { this.metadata = metadata; }

    public double getEstimatedSnrDb() { return estimatedSnrDb; }
    public void setEstimatedSnrDb(double estimatedSnrDb) { this.estimatedSnrDb = estimatedSnrDb; }

    public List<Double> getBerEstimate() { return berEstimate; }
    public void setBerEstimate(List<Double> berEstimate) { this.berEstimate = berEstimate; }

    public List<Double> getSnrRange() { return snrRange; }
    public void setSnrRange(List<Double> snrRange) { this.snrRange = snrRange; }

    public List<Double> getIqSamplesI() { return iqSamplesI; }
    public void setIqSamplesI(List<Double> iqSamplesI) { this.iqSamplesI = iqSamplesI; }

    public List<Double> getIqSamplesQ() { return iqSamplesQ; }
    public void setIqSamplesQ(List<Double> iqSamplesQ) { this.iqSamplesQ = iqSamplesQ; }

    public double getBerMatchPercentage() { return berMatchPercentage; }
    public void setBerMatchPercentage(double berMatchPercentage) { this.berMatchPercentage = berMatchPercentage; }

    public List<Double> getSimulatedBer() { return simulatedBer; }
    public void setSimulatedBer(List<Double> simulatedBer) { this.simulatedBer = simulatedBer; }
}

package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class PythonAnalysisResult {
    @NotNull
    @Min(0)
    private double estimatedSnrDb;
    private List<Double> berEstimate;
    private List<Double> snrRange;
    private List<Double> iqSamplesI;
    private List<Double> iqSamplesQ;
    private long numTotalSamples;
    @NotNull
    @Min(0)
    private double signalPowerDbm;

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

    public long getNumTotalSamples() { return numTotalSamples; }
    public void setNumTotalSamples(long numTotalSamples) { this.numTotalSamples = numTotalSamples; }

    public double getSignalPowerDbm() { return signalPowerDbm; }
    public void setSignalPowerDbm(double signalPowerDbm) { this.signalPowerDbm = signalPowerDbm; }
}

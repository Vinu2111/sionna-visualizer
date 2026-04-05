package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class SigmfMetadata {
    @NotNull
    @Min(0)
    private double sampleRate;
    @NotNull
    @Min(0)
    private double centerFrequency;
    @NotBlank
    private String dataType;
    @NotBlank
    private String description;
    @NotBlank
    private String hardware;
    @NotBlank
    private String author;
    private long numSamples;

    public double getSampleRate() { return sampleRate; }
    public void setSampleRate(double sampleRate) { this.sampleRate = sampleRate; }

    public double getCenterFrequency() { return centerFrequency; }
    public void setCenterFrequency(double centerFrequency) { this.centerFrequency = centerFrequency; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHardware() { return hardware; }
    public void setHardware(String hardware) { this.hardware = hardware; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public long getNumSamples() { return numSamples; }
    public void setNumSamples(long numSamples) { this.numSamples = numSamples; }
}

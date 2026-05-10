package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class ChannelModelRequest {
    @NotBlank
    private String channelModel;
    @NotBlank
    private String modulation;
    @NotNull
    @Min(0)
    private double snrMin;
    @NotNull
    @Min(0)
    private double snrMax;
    @NotNull
    @Min(0)
    private int snrSteps;
    @NotNull
    @Min(0)
    private int numAntennasTx;
    @NotNull
    @Min(0)
    private int numAntennasRx;
    @NotNull
    @Min(0)
    private double carrierFrequency;
    @NotNull
    @Min(0)
    private double delaySpread;
    @NotNull
    @Min(0)
    private int numTimeSteps;

    // Normal standard getter & setters mappings for Jackson translations automatically
    public String getChannelModel() { return channelModel; }
    public void setChannelModel(String channelModel) { this.channelModel = channelModel; }

    public String getModulation() { return modulation; }
    public void setModulation(String modulation) { this.modulation = modulation; }

    public double getSnrMin() { return snrMin; }
    public void setSnrMin(double snrMin) { this.snrMin = snrMin; }

    public double getSnrMax() { return snrMax; }
    public void setSnrMax(double snrMax) { this.snrMax = snrMax; }

    public int getSnrSteps() { return snrSteps; }
    public void setSnrSteps(int snrSteps) { this.snrSteps = snrSteps; }

    public int getNumAntennasTx() { return numAntennasTx; }
    public void setNumAntennasTx(int numAntennasTx) { this.numAntennasTx = numAntennasTx; }

    public int getNumAntennasRx() { return numAntennasRx; }
    public void setNumAntennasRx(int numAntennasRx) { this.numAntennasRx = numAntennasRx; }

    public double getCarrierFrequency() { return carrierFrequency; }
    public void setCarrierFrequency(double carrierFrequency) { this.carrierFrequency = carrierFrequency; }

    public double getDelaySpread() { return delaySpread; }
    public void setDelaySpread(double delaySpread) { this.delaySpread = delaySpread; }

    public int getNumTimeSteps() { return numTimeSteps; }
    public void setNumTimeSteps(int numTimeSteps) { this.numTimeSteps = numTimeSteps; }
}

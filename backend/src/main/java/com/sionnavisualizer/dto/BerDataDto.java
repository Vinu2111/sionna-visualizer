package com.sionnavisualizer.dto;

import java.util.List;

public class BerDataDto {
    private List<Double> snrRangeDb;
    private List<Double> simulatedBer;
    private List<Double> theoreticalBer;
    private String modulation;
    private String channelModel;
    private Double frequencyGhz;
    private Integer numAntennasTx;
    private Integer numAntennasRx;

    public List<Double> getSnrRangeDb() { return snrRangeDb; }
    public void setSnrRangeDb(List<Double> snrRangeDb) { this.snrRangeDb = snrRangeDb; }

    public List<Double> getSimulatedBer() { return simulatedBer; }
    public void setSimulatedBer(List<Double> simulatedBer) { this.simulatedBer = simulatedBer; }

    public List<Double> getTheoreticalBer() { return theoreticalBer; }
    public void setTheoreticalBer(List<Double> theoreticalBer) { this.theoreticalBer = theoreticalBer; }

    public String getModulation() { return modulation; }
    public void setModulation(String modulation) { this.modulation = modulation; }

    public String getChannelModel() { return channelModel; }
    public void setChannelModel(String channelModel) { this.channelModel = channelModel; }

    public Double getFrequencyGhz() { return frequencyGhz; }
    public void setFrequencyGhz(Double frequencyGhz) { this.frequencyGhz = frequencyGhz; }

    public Integer getNumAntennasTx() { return numAntennasTx; }
    public void setNumAntennasTx(Integer numAntennasTx) { this.numAntennasTx = numAntennasTx; }

    public Integer getNumAntennasRx() { return numAntennasRx; }
    public void setNumAntennasRx(Integer numAntennasRx) { this.numAntennasRx = numAntennasRx; }
}

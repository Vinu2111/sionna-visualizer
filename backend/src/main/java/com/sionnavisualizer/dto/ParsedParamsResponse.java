package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ParsedParamsResponse {

    // Core simulation parameters extracted by Claude AI
    @NotNull
    @Min(0)
    private Double frequency;
    @NotBlank
    private String channelModel;
    @NotBlank
    private String modulation;
    private List<String> modulationList;
    @NotNull
    @Min(0)
    private Integer numAntennasTx;
    @NotNull
    @Min(0)
    private Integer numAntennasRx;
    @NotNull
    @Min(0)
    private Double snrMin;
    @NotNull
    @Min(0)
    private Double snrMax;
    @NotBlank
    private String simulationType;
    @NotBlank
    private String environment;
    private Boolean runComparison;

    // Metadata about the AI parsing result
    @NotBlank
    private String confidence;               // HIGH | MEDIUM | LOW
    private List<String> missingParams;      // Parameters Claude could not find
    @NotBlank
    private String naturalLanguageSummary;   // One-sentence human summary from Claude
    private List<String> aiFilled;          // Which fields were filled by AI (for UI badges)

    public Double getFrequency() { return frequency; }
    public void setFrequency(Double frequency) { this.frequency = frequency; }

    public String getChannelModel() { return channelModel; }
    public void setChannelModel(String channelModel) { this.channelModel = channelModel; }

    public String getModulation() { return modulation; }
    public void setModulation(String modulation) { this.modulation = modulation; }

    public List<String> getModulationList() { return modulationList; }
    public void setModulationList(List<String> modulationList) { this.modulationList = modulationList; }

    public Integer getNumAntennasTx() { return numAntennasTx; }
    public void setNumAntennasTx(Integer numAntennasTx) { this.numAntennasTx = numAntennasTx; }

    public Integer getNumAntennasRx() { return numAntennasRx; }
    public void setNumAntennasRx(Integer numAntennasRx) { this.numAntennasRx = numAntennasRx; }

    public Double getSnrMin() { return snrMin; }
    public void setSnrMin(Double snrMin) { this.snrMin = snrMin; }

    public Double getSnrMax() { return snrMax; }
    public void setSnrMax(Double snrMax) { this.snrMax = snrMax; }

    public String getSimulationType() { return simulationType; }
    public void setSimulationType(String simulationType) { this.simulationType = simulationType; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public Boolean getRunComparison() { return runComparison; }
    public void setRunComparison(Boolean runComparison) { this.runComparison = runComparison; }

    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }

    public List<String> getMissingParams() { return missingParams; }
    public void setMissingParams(List<String> missingParams) { this.missingParams = missingParams; }

    public String getNaturalLanguageSummary() { return naturalLanguageSummary; }
    public void setNaturalLanguageSummary(String naturalLanguageSummary) { this.naturalLanguageSummary = naturalLanguageSummary; }

    public List<String> getAiFilled() { return aiFilled; }
    public void setAiFilled(List<String> aiFilled) { this.aiFilled = aiFilled; }
}

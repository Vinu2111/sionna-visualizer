package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.util.List;

public class MeasurementOverlayRequestDto {

    @JsonProperty("simulation_type")
    private String simulationType = "AWGN";

    @JsonProperty("simulation_id")
    @NotNull
    @Min(0)
    private Long simulationId;

    @JsonProperty("measurements")
    @NotNull
    @Size(min = 2, max = 50, message = "Must provide between 2 and 50 measurement points")
    private List<MeasurementPointDto> measurements;

    @JsonProperty("frequency_ghz")
    private double frequencyGhz = 28.0;

    @JsonProperty("environment")
    private String environment = "urban";

    public String getSimulationType() { return simulationType; }
    public void setSimulationType(String simulationType) { this.simulationType = simulationType; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public List<MeasurementPointDto> getMeasurements() { return measurements; }
    public void setMeasurements(List<MeasurementPointDto> measurements) { this.measurements = measurements; }

    public double getFrequencyGhz() { return frequencyGhz; }
    public void setFrequencyGhz(double frequencyGhz) { this.frequencyGhz = frequencyGhz; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
}

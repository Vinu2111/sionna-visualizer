package com.sionnavisualizer.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PathLossRequestDto {

    @NotNull(message = "Number of paths is required")
    private Integer numPaths;

    @NotNull(message = "Frequency is required")
    @Min(value = 1, message = "Frequency must be at least 1 GHz")
    @Max(value = 100, message = "Frequency must not exceed 100 GHz")
    private Double frequencyGhz;

    @NotNull(message = "Environment is required")
    @Pattern(regexp = "^(?i)(urban|suburban|rural)$", message = "Environment must be urban, suburban, or rural")
    private String environment;

    private String colormap = "default";

    public PathLossRequestDto() {}

    public Integer getNum_paths() { return numPaths; }
    public void setNum_paths(Integer numPaths) { this.numPaths = numPaths; }

    public Double getFrequency_ghz() { return frequencyGhz; }
    public void setFrequency_ghz(Double frequencyGhz) { this.frequencyGhz = frequencyGhz; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getColormap() { return colormap; }
    public void setColormap(String colormap) { this.colormap = colormap; }
}

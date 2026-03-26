package com.sionnavisualizer.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ModulationComparisonRequestDto {
    
    @DecimalMin("-20.0") @DecimalMax("0.0")
    private double snr_min = -5.0;
    
    @DecimalMin("0.0") @DecimalMax("40.0")
    private double snr_max = 25.0;
    
    @Min(10) @Max(100)
    private int snr_steps = 50;

    private String colormap = "default";

    public ModulationComparisonRequestDto() {}

    public ModulationComparisonRequestDto(double snr_min, double snr_max, int snr_steps) {
        this.snr_min = snr_min;
        this.snr_max = snr_max;
        this.snr_steps = snr_steps;
    }

    public double getSnr_min() { return snr_min; }
    public void setSnr_min(double snr_min) { this.snr_min = snr_min; }

    public double getSnr_max() { return snr_max; }
    public void setSnr_max(double snr_max) { this.snr_max = snr_max; }

    public int getSnr_steps() { return snr_steps; }
    public void setSnr_steps(int snr_steps) { this.snr_steps = snr_steps; }

    public String getColormap() { return colormap; }
    public void setColormap(String colormap) { this.colormap = colormap; }
}

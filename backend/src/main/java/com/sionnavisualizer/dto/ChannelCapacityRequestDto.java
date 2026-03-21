package com.sionnavisualizer.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;

public class ChannelCapacityRequestDto {

    @DecimalMin("-20.0") @DecimalMax("10.0")
    private double snr_min = -10.0;

    @DecimalMin("10.0") @DecimalMax("50.0")
    private double snr_max = 30.0;

    @Min(20) @Max(100)
    private int snr_steps = 50;

    @NotEmpty
    private List<Double> bandwidths_mhz = Arrays.asList(10.0, 100.0, 400.0, 1000.0);

    public ChannelCapacityRequestDto() {}

    public ChannelCapacityRequestDto(double snr_min, double snr_max, int snr_steps, List<Double> bandwidths_mhz) {
        this.snr_min = snr_min;
        this.snr_max = snr_max;
        this.snr_steps = snr_steps;
        this.bandwidths_mhz = bandwidths_mhz;
    }

    public double getSnr_min() { return snr_min; }
    public void setSnr_min(double snr_min) { this.snr_min = snr_min; }

    public double getSnr_max() { return snr_max; }
    public void setSnr_max(double snr_max) { this.snr_max = snr_max; }

    public int getSnr_steps() { return snr_steps; }
    public void setSnr_steps(int snr_steps) { this.snr_steps = snr_steps; }

    public List<Double> getBandwidths_mhz() { return bandwidths_mhz; }
    public void setBandwidths_mhz(List<Double> bandwidths_mhz) { this.bandwidths_mhz = bandwidths_mhz; }
}

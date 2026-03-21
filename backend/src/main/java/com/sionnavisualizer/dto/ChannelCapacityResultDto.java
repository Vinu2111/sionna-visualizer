package com.sionnavisualizer.dto;

import java.util.List;
import java.util.Map;

public class ChannelCapacityResultDto {
    private List<Double> snr_db;
    private List<Double> spectral_efficiency;
    private List<Map<String, Object>> capacity_curves;
    private double snr_min;
    private double snr_max;
    private Map<String, Object> insights;

    public ChannelCapacityResultDto() {}

    public List<Double> getSnr_db() { return snr_db; }
    public void setSnr_db(List<Double> snr_db) { this.snr_db = snr_db; }

    public List<Double> getSpectral_efficiency() { return spectral_efficiency; }
    public void setSpectral_efficiency(List<Double> spectral_efficiency) { this.spectral_efficiency = spectral_efficiency; }

    public List<Map<String, Object>> getCapacity_curves() { return capacity_curves; }
    public void setCapacity_curves(List<Map<String, Object>> capacity_curves) { this.capacity_curves = capacity_curves; }

    public double getSnr_min() { return snr_min; }
    public void setSnr_min(double snr_min) { this.snr_min = snr_min; }

    public double getSnr_max() { return snr_max; }
    public void setSnr_max(double snr_max) { this.snr_max = snr_max; }

    public Map<String, Object> getInsights() { return insights; }
    public void setInsights(Map<String, Object> insights) { this.insights = insights; }
}

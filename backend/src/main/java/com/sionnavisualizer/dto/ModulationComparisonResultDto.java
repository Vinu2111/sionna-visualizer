package com.sionnavisualizer.dto;

import java.util.List;
import java.util.Map;

public class ModulationComparisonResultDto {
    private List<Double> snr_db;
    private List<Double> bpsk;
    private List<Double> qpsk;
    private List<Double> qam16;
    private List<Double> qam64;
    private double snr_min;
    private double snr_max;
    private int snr_steps;
    private Map<String, Object> crossover_points;
    private PerformanceDto performance;
    private List<String> colors;
    private String colormap_used;

    public List<Double> getSnr_db() { return snr_db; }
    public void setSnr_db(List<Double> snr_db) { this.snr_db = snr_db; }

    public List<Double> getBpsk() { return bpsk; }
    public void setBpsk(List<Double> bpsk) { this.bpsk = bpsk; }

    public List<Double> getQpsk() { return qpsk; }
    public void setQpsk(List<Double> qpsk) { this.qpsk = qpsk; }

    public List<Double> getQam16() { return qam16; }
    public void setQam16(List<Double> qam16) { this.qam16 = qam16; }

    public List<Double> getQam64() { return qam64; }
    public void setQam64(List<Double> qam64) { this.qam64 = qam64; }

    public double getSnr_min() { return snr_min; }
    public void setSnr_min(double snr_min) { this.snr_min = snr_min; }

    public double getSnr_max() { return snr_max; }
    public void setSnr_max(double snr_max) { this.snr_max = snr_max; }

    public int getSnr_steps() { return snr_steps; }
    public void setSnr_steps(int snr_steps) { this.snr_steps = snr_steps; }

    public Map<String, Object> getCrossover_points() { return crossover_points; }
    public void setCrossover_points(Map<String, Object> crossover_points) { this.crossover_points = crossover_points; }

    public PerformanceDto getPerformance() { return performance; }
    public void setPerformance(PerformanceDto performance) { this.performance = performance; }

    public List<String> getColors() { return colors; }
    public void setColors(List<String> colors) { this.colors = colors; }

    public String getColormap_used() { return colormap_used; }
    public void setColormap_used(String colormap_used) { this.colormap_used = colormap_used; }
}

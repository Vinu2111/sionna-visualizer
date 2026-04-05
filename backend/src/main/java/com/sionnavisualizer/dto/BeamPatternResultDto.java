package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.List;

public class BeamPatternResultDto {
    private Long id;
    private List<Double> angles;
    private List<Double> pattern_db;
    @NotNull
    @Min(0)
    private double steering_angle;
    @NotNull
    @Min(0)
    private int num_antennas;
    @NotNull
    @Min(0)
    private double frequency_ghz;
    @NotNull
    @Min(0)
    private double main_lobe_width;
    @NotNull
    @Min(0)
    private double side_lobe_level;
    @NotNull
    @Min(0)
    private double array_gain_db;
    
    private PerformanceDto performance;
    private List<String> colors;
    @NotBlank
    private String colormap_used;

    public BeamPatternResultDto() {
    }

    public List<Double> getAngles() {
        return angles;
    }

    public void setAngles(List<Double> angles) {
        this.angles = angles;
    }

    public List<Double> getPattern_db() {
        return pattern_db;
    }

    public void setPattern_db(List<Double> pattern_db) {
        this.pattern_db = pattern_db;
    }

    public double getSteering_angle() {
        return steering_angle;
    }

    public void setSteering_angle(double steering_angle) {
        this.steering_angle = steering_angle;
    }

    public int getNum_antennas() {
        return num_antennas;
    }

    public void setNum_antennas(int num_antennas) {
        this.num_antennas = num_antennas;
    }

    public double getFrequency_ghz() {
        return frequency_ghz;
    }

    public void setFrequency_ghz(double frequency_ghz) {
        this.frequency_ghz = frequency_ghz;
    }

    public double getMain_lobe_width() {
        return main_lobe_width;
    }

    public void setMain_lobe_width(double main_lobe_width) {
        this.main_lobe_width = main_lobe_width;
    }

    public double getSide_lobe_level() {
        return side_lobe_level;
    }

    public void setSide_lobe_level(double side_lobe_level) {
        this.side_lobe_level = side_lobe_level;
    }

    public double getArray_gain_db() {
        return array_gain_db;
    }

    public void setArray_gain_db(double array_gain_db) {
        this.array_gain_db = array_gain_db;
    }

    public PerformanceDto getPerformance() { return performance; }
    public void setPerformance(PerformanceDto performance) { this.performance = performance; }

    public List<String> getColors() { return colors; }
    public void setColors(List<String> colors) { this.colors = colors; }

    public String getColormap_used() { return colormap_used; }
    public void setColormap_used(String colormap_used) { this.colormap_used = colormap_used; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}

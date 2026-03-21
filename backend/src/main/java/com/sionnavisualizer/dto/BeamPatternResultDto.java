package com.sionnavisualizer.dto;

import java.util.List;

public class BeamPatternResultDto {
    private List<Double> angles;
    private List<Double> pattern_db;
    private double steering_angle;
    private int num_antennas;
    private double frequency_ghz;
    private double main_lobe_width;
    private double side_lobe_level;
    private double array_gain_db;

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
}

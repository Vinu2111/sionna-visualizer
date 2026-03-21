package com.sionnavisualizer.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class BeamPatternRequestDto {
    
    @Min(2) @Max(128)
    private int num_antennas = 16;
    
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private double steering_angle = 0.0;
    
    @DecimalMin("1.0") @DecimalMax("100.0")
    private double frequency_ghz = 28.0;
    
    @DecimalMin("0.1") @DecimalMax("2.0")
    private double array_spacing = 0.5;

    public BeamPatternRequestDto() {
    }

    public BeamPatternRequestDto(int num_antennas, double steering_angle, double frequency_ghz, double array_spacing) {
        this.num_antennas = num_antennas;
        this.steering_angle = steering_angle;
        this.frequency_ghz = frequency_ghz;
        this.array_spacing = array_spacing;
    }

    public int getNum_antennas() {
        return num_antennas;
    }

    public void setNum_antennas(int num_antennas) {
        this.num_antennas = num_antennas;
    }

    public double getSteering_angle() {
        return steering_angle;
    }

    public void setSteering_angle(double steering_angle) {
        this.steering_angle = steering_angle;
    }

    public double getFrequency_ghz() {
        return frequency_ghz;
    }

    public void setFrequency_ghz(double frequency_ghz) {
        this.frequency_ghz = frequency_ghz;
    }

    public double getArray_spacing() {
        return array_spacing;
    }

    public void setArray_spacing(double array_spacing) {
        this.array_spacing = array_spacing;
    }
}

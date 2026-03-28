package com.sionnavisualizer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.util.List;

public class SinrSteeringRequestDto {

    @JsonProperty("num_antennas")
    private int numAntennas = 16;

    @JsonProperty("frequency_ghz")
    private double frequencyGhz = 28.0;

    @JsonProperty("steering_angles")
    private List<Double> steeringAngles = java.util.Arrays.asList(-60.0, -45.0, -30.0, -15.0, 0.0, 15.0, 30.0, 45.0, 60.0);

    @JsonProperty("interference_angle_deg")
    private double interferenceAngleDeg = 45.0;

    @JsonProperty("signal_power_dbm")
    private double signalPowerDbm = 0.0;

    @JsonProperty("interference_power_dbm")
    private double interferencePowerDbm = -10.0;

    public int getNumAntennas() { return numAntennas; }
    public void setNumAntennas(int v) { this.numAntennas = v; }

    public double getFrequencyGhz() { return frequencyGhz; }
    public void setFrequencyGhz(double v) { this.frequencyGhz = v; }

    public List<Double> getSteeringAngles() { return steeringAngles; }
    public void setSteeringAngles(List<Double> v) { this.steeringAngles = v; }

    public double getInterferenceAngleDeg() { return interferenceAngleDeg; }
    public void setInterferenceAngleDeg(double v) { this.interferenceAngleDeg = v; }

    public double getSignalPowerDbm() { return signalPowerDbm; }
    public void setSignalPowerDbm(double v) { this.signalPowerDbm = v; }

    public double getInterferencePowerDbm() { return interferencePowerDbm; }
    public void setInterferencePowerDbm(double v) { this.interferencePowerDbm = v; }
}

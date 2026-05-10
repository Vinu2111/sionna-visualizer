package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

import lombok.Data;
import java.util.List;

@Data
public class WaypointDto {
    private List<Double> position;
    @NotNull
    @Min(0)
    private double distanceM;
    @NotNull
    @Min(0)
    private double signalDbm;
    private boolean handoverRequired;
    @NotNull
    @Min(0)
    private double timeS;
    private List<Double> velocityVector;
}

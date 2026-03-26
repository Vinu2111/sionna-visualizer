package com.sionnavisualizer.dto;

import lombok.Data;
import java.util.List;

@Data
public class WaypointDto {
    private List<Double> position;
    private double distanceM;
    private double signalDbm;
    private boolean handoverRequired;
    private double timeS;
    private List<Double> velocityVector;
}

package com.sionnavisualizer.dto;

import lombok.Data;
import java.util.List;

@Data
public class UeTrajectoryResultDto {
    private List<WaypointDto> waypoints;
    private List<Double> txPosition;
    private String trajectoryType;
    private UeTrajectorySummaryDto summary;
    private PerformanceDto performance;
    private List<String> colors;
    private String colormapUsed;
}

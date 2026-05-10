package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import lombok.Data;
import java.util.List;

@Data
public class UeTrajectoryResultDto {
    private Long id;
    private List<WaypointDto> waypoints;
    private List<Double> txPosition;
    @NotBlank
    private String trajectoryType;
    private UeTrajectorySummaryDto summary;
    private PerformanceDto performance;
    private List<String> colors;
    @NotBlank
    private String colormapUsed;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}

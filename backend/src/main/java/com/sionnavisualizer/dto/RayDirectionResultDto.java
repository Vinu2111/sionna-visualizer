package com.sionnavisualizer.dto;

import lombok.Data;
import java.util.List;

@Data
public class RayDirectionResultDto {
    private List<RayPathDto> paths;
    private List<Double> tx_position;
    private List<Double> rx_position;
    private Double los_distance_m;
    private RayDirectionSummaryDto summary;
    private PerformanceDto performance;
    private List<String> colors;
    private String colormap_used;
}

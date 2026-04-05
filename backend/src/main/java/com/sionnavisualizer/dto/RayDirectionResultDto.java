package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import lombok.Data;
import java.util.List;

@Data
public class RayDirectionResultDto {
    private Long id;
    private List<RayPathDto> paths;
    private List<Double> tx_position;
    private List<Double> rx_position;
    @NotNull
    @Min(0)
    private Double los_distance_m;
    private RayDirectionSummaryDto summary;
    private PerformanceDto performance;
    private List<String> colors;
    @NotBlank
    private String colormap_used;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}

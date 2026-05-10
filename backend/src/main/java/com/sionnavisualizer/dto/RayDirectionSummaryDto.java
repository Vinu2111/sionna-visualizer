package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class RayDirectionSummaryDto {
    @NotNull
    @Min(0)
    private Double angular_spread_deg;
    @NotNull
    @Min(0)
    private Double mean_departure_azimuth;
    @NotNull
    @Min(0)
    private Double mean_arrival_azimuth;
    @NotNull
    @Min(0)
    private Integer num_los_paths;
    @NotNull
    @Min(0)
    private Integer num_nlos_paths;
}

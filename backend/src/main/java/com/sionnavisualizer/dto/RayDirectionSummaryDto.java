package com.sionnavisualizer.dto;

import lombok.Data;

@Data
public class RayDirectionSummaryDto {
    private Double angular_spread_deg;
    private Double mean_departure_azimuth;
    private Double mean_arrival_azimuth;
    private Integer num_los_paths;
    private Integer num_nlos_paths;
}

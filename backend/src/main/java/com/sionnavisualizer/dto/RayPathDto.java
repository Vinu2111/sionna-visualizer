package com.sionnavisualizer.dto;

import lombok.Data;

@Data
public class RayPathDto {
    private Integer path_id;
    private Double departure_azimuth_deg;
    private Double departure_elevation_deg;
    private Double arrival_azimuth_deg;
    private Double arrival_elevation_deg;
    private Double path_loss_db;
    private Double delay_ns;
    private String path_type;
}

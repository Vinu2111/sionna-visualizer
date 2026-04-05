package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class RayPathDto {
    @NotNull
    @Min(0)
    private Integer path_id;
    @NotNull
    @Min(0)
    private Double departure_azimuth_deg;
    @NotNull
    @Min(0)
    private Double departure_elevation_deg;
    @NotNull
    @Min(0)
    private Double arrival_azimuth_deg;
    @NotNull
    @Min(0)
    private Double arrival_elevation_deg;
    @NotNull
    @Min(0)
    private Double path_loss_db;
    @NotNull
    @Min(0)
    private Double delay_ns;
    @NotBlank
    private String path_type;
}

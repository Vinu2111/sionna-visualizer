package com.sionnavisualizer.dto;

import lombok.Data;
import java.util.List;

@Data
public class RayDirectionRequestDto {
    private Integer num_paths = 8;
    private Double frequency_ghz = 28.0;
    private String environment = "urban";
    private List<Double> tx_position = List.of(0.0, 0.0, 10.0);
    private List<Double> rx_position = List.of(100.0, 50.0, 1.5);
    private String colormap = "default";
}

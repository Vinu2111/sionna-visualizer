package com.sionnavisualizer.dto;

import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class UeTrajectoryRequestDto {
    @Min(4)
    @Max(10)
    private int numWaypoints = 6;

    @Min(1)
    @Max(100)
    private double frequencyGhz = 28.0;

    @Pattern(regexp = "^(urban|suburban|rural)$", message = "Environment must be urban, suburban, or rural")
    private String environment = "urban";

    @NotNull
    private List<Double> txPosition = List.of(0.0, 0.0, 25.0);

    @Min(5)
    @Max(120)
    private double speedKmh = 30.0;

    @Pattern(regexp = "^(random|linear|circular)$", message = "Trajectory type must be random, linear, or circular")
    private String trajectoryType = "random";

    private String colormap = "default";
}

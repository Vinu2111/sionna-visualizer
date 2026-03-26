package com.sionnavisualizer.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
public class SimulationEstimateRequestDto {
    @NotNull
    private String simulation_type;
    private Map<String, Object> parameters;
}

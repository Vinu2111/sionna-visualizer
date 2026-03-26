package com.sionnavisualizer.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class SimulationEstimateResultDto {
    private String simulation_type;
    private int estimated_ms;
    private EstimateRangeDto estimated_range;
    private String complexity_label;
    private String complexity_color;
    private List<String> tips;
    private Map<String, Object> parameters_received;
}

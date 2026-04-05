package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class SimulationEstimateResultDto {
    private Long id;
    @NotBlank
    private String simulation_type;
    @NotNull
    @Min(0)
    private int estimated_ms;
    private EstimateRangeDto estimated_range;
    @NotBlank
    private String complexity_label;
    @NotBlank
    private String complexity_color;
    private List<String> tips;
    private Map<String, Object> parameters_received;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}

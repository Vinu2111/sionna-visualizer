package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class PerformanceDto {
    @NotNull
    @Min(0)
    private Long duration_ms;
    @NotBlank
    private String compute_type;
    @NotNull
    @Min(0)
    private Double memory_mb;
    @NotBlank
    private String sionna_version;

    public Long getDuration_ms() { return duration_ms; }
    public void setDuration_ms(Long duration_ms) { this.duration_ms = duration_ms; }

    public String getCompute_type() { return compute_type; }
    public void setCompute_type(String compute_type) { this.compute_type = compute_type; }

    public Double getMemory_mb() { return memory_mb; }
    public void setMemory_mb(Double memory_mb) { this.memory_mb = memory_mb; }

    public String getSionna_version() { return sionna_version; }
    public void setSionna_version(String sionna_version) { this.sionna_version = sionna_version; }
}

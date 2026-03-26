package com.sionnavisualizer.dto;

public class PerformanceDto {
    private Long duration_ms;
    private String compute_type;
    private Double memory_mb;
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

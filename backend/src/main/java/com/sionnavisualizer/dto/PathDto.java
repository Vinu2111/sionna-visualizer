package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class PathDto {
    @NotNull
    @Min(0)
    private Integer pathId;
    @NotNull
    @Min(0)
    private Double distanceM;
    @NotNull
    @Min(0)
    private Double pathLossDb;
    @NotBlank
    private String pathType;
    @NotNull
    @Min(0)
    private Double delayNs;

    public PathDto() {}

    public Integer getPath_id() { return pathId; }
    public void setPath_id(Integer pathId) { this.pathId = pathId; }

    public Double getDistance_m() { return distanceM; }
    public void setDistance_m(Double distanceM) { this.distanceM = distanceM; }

    public Double getPath_loss_db() { return pathLossDb; }
    public void setPath_loss_db(Double pathLossDb) { this.pathLossDb = pathLossDb; }

    public String getPath_type() { return pathType; }
    public void setPath_type(String pathType) { this.pathType = pathType; }

    public Double getDelay_ns() { return delayNs; }
    public void setDelay_ns(Double delayNs) { this.delayNs = delayNs; }
}

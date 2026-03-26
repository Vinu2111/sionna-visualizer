package com.sionnavisualizer.dto;

public class PathDto {
    private Integer pathId;
    private Double distanceM;
    private Double pathLossDb;
    private String pathType;
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

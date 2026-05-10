package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

public class PathLossSummaryDto {
    @NotNull
    @Min(0)
    private Double losPathLossDb;
    @NotNull
    @Min(0)
    private Double maxPathLossDb;
    @NotNull
    @Min(0)
    private Double pathLossSpreadDb;
    @NotNull
    @Min(0)
    private Double meanDelayNs;

    public PathLossSummaryDto() {}

    public Double getLos_path_loss_db() { return losPathLossDb; }
    public void setLos_path_loss_db(Double losPathLossDb) { this.losPathLossDb = losPathLossDb; }

    public Double getMax_path_loss_db() { return maxPathLossDb; }
    public void setMax_path_loss_db(Double maxPathLossDb) { this.maxPathLossDb = maxPathLossDb; }

    public Double getPath_loss_spread_db() { return pathLossSpreadDb; }
    public void setPath_loss_spread_db(Double pathLossSpreadDb) { this.pathLossSpreadDb = pathLossSpreadDb; }

    public Double getMean_delay_ns() { return meanDelayNs; }
    public void setMean_delay_ns(Double meanDelayNs) { this.meanDelayNs = meanDelayNs; }
}

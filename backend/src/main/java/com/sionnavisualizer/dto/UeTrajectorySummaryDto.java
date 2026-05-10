package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class UeTrajectorySummaryDto {
    @NotNull
    @Min(0)
    private double totalDistanceM;
    @NotNull
    @Min(0)
    private double totalTimeS;
    @NotNull
    @Min(0)
    private double minSignalDbm;
    @NotNull
    @Min(0)
    private double maxSignalDbm;
    @NotNull
    @Min(0)
    private int handoverCount;
    @NotNull
    @Min(0)
    private double coveragePercent;
}

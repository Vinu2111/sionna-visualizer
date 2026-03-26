package com.sionnavisualizer.dto;

import lombok.Data;

@Data
public class UeTrajectorySummaryDto {
    private double totalDistanceM;
    private double totalTimeS;
    private double minSignalDbm;
    private double maxSignalDbm;
    private int handoverCount;
    private double coveragePercent;
}

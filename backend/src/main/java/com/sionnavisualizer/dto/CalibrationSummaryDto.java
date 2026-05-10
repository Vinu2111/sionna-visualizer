package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CalibrationSummaryDto {

    @JsonProperty("mean_absolute_error")
    @NotNull
    @Min(0)
    private double meanAbsoluteError;

    @JsonProperty("rmse")
    @NotNull
    @Min(0)
    private double rmse;

    @JsonProperty("calibration_quality")
    @NotBlank
    private String calibrationQuality;

    @JsonProperty("systematic_offset_db")
    @NotNull
    @Min(0)
    private double systematicOffsetDb;

    @JsonProperty("max_error_point")
    @NotNull
    @Min(0)
    private double maxErrorPoint;

    @JsonProperty("num_measurement_points")
    @NotNull
    @Min(0)
    private int numMeasurementPoints;

    public double getMeanAbsoluteError() { return meanAbsoluteError; }
    public void setMeanAbsoluteError(double v) { this.meanAbsoluteError = v; }

    public double getRmse() { return rmse; }
    public void setRmse(double v) { this.rmse = v; }

    public String getCalibrationQuality() { return calibrationQuality; }
    public void setCalibrationQuality(String v) { this.calibrationQuality = v; }

    public double getSystematicOffsetDb() { return systematicOffsetDb; }
    public void setSystematicOffsetDb(double v) { this.systematicOffsetDb = v; }

    public double getMaxErrorPoint() { return maxErrorPoint; }
    public void setMaxErrorPoint(double v) { this.maxErrorPoint = v; }

    public int getNumMeasurementPoints() { return numMeasurementPoints; }
    public void setNumMeasurementPoints(int v) { this.numMeasurementPoints = v; }
}

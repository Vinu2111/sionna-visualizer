package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ComparisonPointDto {

    @JsonProperty("snr_db")
    @NotNull
    @Min(0)
    private double snrDb;

    @JsonProperty("ber_simulated")
    @NotNull
    @Min(0)
    private double berSimulated;

    @JsonProperty("ber_measured")
    @NotNull
    @Min(0)
    private double berMeasured;

    @JsonProperty("absolute_error")
    @NotNull
    @Min(0)
    private double absoluteError;

    @JsonProperty("relative_error_percent")
    @NotNull
    @Min(0)
    private double relativeErrorPercent;

    @JsonProperty("error_db")
    @NotNull
    @Min(0)
    private Double errorDb;

    @JsonProperty("location")
    @NotBlank
    private String location;

    public double getSnrDb() { return snrDb; }
    public void setSnrDb(double snrDb) { this.snrDb = snrDb; }

    public double getBerSimulated() { return berSimulated; }
    public void setBerSimulated(double berSimulated) { this.berSimulated = berSimulated; }

    public double getBerMeasured() { return berMeasured; }
    public void setBerMeasured(double berMeasured) { this.berMeasured = berMeasured; }

    public double getAbsoluteError() { return absoluteError; }
    public void setAbsoluteError(double absoluteError) { this.absoluteError = absoluteError; }

    public double getRelativeErrorPercent() { return relativeErrorPercent; }
    public void setRelativeErrorPercent(double relativeErrorPercent) { this.relativeErrorPercent = relativeErrorPercent; }

    public Double getErrorDb() { return errorDb; }
    public void setErrorDb(Double errorDb) { this.errorDb = errorDb; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}

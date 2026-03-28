package com.sionnavisualizer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ComparisonPointDto {

    @JsonProperty("snr_db")
    private double snrDb;

    @JsonProperty("ber_simulated")
    private double berSimulated;

    @JsonProperty("ber_measured")
    private double berMeasured;

    @JsonProperty("absolute_error")
    private double absoluteError;

    @JsonProperty("relative_error_percent")
    private double relativeErrorPercent;

    @JsonProperty("error_db")
    private Double errorDb;

    @JsonProperty("location")
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

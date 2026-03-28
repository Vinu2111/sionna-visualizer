package com.sionnavisualizer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeasurementPointDto {

    @JsonProperty("snr_db")
    private double snrDb;

    @JsonProperty("ber_measured")
    private double berMeasured;

    @JsonProperty("location")
    private String location = "";

    public double getSnrDb() { return snrDb; }
    public void setSnrDb(double snrDb) { this.snrDb = snrDb; }

    public double getBerMeasured() { return berMeasured; }
    public void setBerMeasured(double berMeasured) { this.berMeasured = berMeasured; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}

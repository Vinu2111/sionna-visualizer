package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

public class ThzRequest {
    @NotNull
    @Min(0)
    private Double frequency_ghz;
    @NotNull
    @Min(0)
    private Double humidity_percent;
    @NotNull
    @Min(0)
    private Double temperature_celsius;
    @NotNull
    @Min(0)
    private Double pressure_hpa;
    @NotNull
    @Min(0)
    private Double rain_rate_mm_per_hr;
    @NotNull
    @Min(0)
    private Double link_distance_meters;
    @NotNull
    @Min(0)
    private Double tx_power_dbm;

    public Double getFrequency_ghz() { return frequency_ghz; }
    public void setFrequency_ghz(Double frequency_ghz) { this.frequency_ghz = frequency_ghz; }

    public Double getHumidity_percent() { return humidity_percent; }
    public void setHumidity_percent(Double humidity_percent) { this.humidity_percent = humidity_percent; }

    public Double getTemperature_celsius() { return temperature_celsius; }
    public void setTemperature_celsius(Double temperature_celsius) { this.temperature_celsius = temperature_celsius; }

    public Double getPressure_hpa() { return pressure_hpa; }
    public void setPressure_hpa(Double pressure_hpa) { this.pressure_hpa = pressure_hpa; }

    public Double getRain_rate_mm_per_hr() { return rain_rate_mm_per_hr; }
    public void setRain_rate_mm_per_hr(Double rain_rate_mm_per_hr) { this.rain_rate_mm_per_hr = rain_rate_mm_per_hr; }

    public Double getLink_distance_meters() { return link_distance_meters; }
    public void setLink_distance_meters(Double link_distance_meters) { this.link_distance_meters = link_distance_meters; }

    public Double getTx_power_dbm() { return tx_power_dbm; }
    public void setTx_power_dbm(Double tx_power_dbm) { this.tx_power_dbm = tx_power_dbm; }
}

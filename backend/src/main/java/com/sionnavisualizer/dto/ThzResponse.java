package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Map;

public class ThzResponse {
    @NotNull
    @Min(0)
    private Double molecular_absorption_db_per_km;
    @NotNull
    @Min(0)
    private Double rain_attenuation_db_per_km;
    @NotNull
    @Min(0)
    private Double free_space_path_loss_db;
    @NotNull
    @Min(0)
    private Double total_path_loss_db;
    @NotNull
    @Min(0)
    private Double received_power_dbm;
    private List<Double> ber_at_distances;
    private List<Double> distance_range_meters;
    private List<Map<String, Double>> absorption_spectrum;
    @NotNull
    @Min(0)
    private Double max_viable_range_meters;

    public Double getMolecular_absorption_db_per_km() { return molecular_absorption_db_per_km; }
    public void setMolecular_absorption_db_per_km(Double molecular_absorption_db_per_km) { this.molecular_absorption_db_per_km = molecular_absorption_db_per_km; }

    public Double getRain_attenuation_db_per_km() { return rain_attenuation_db_per_km; }
    public void setRain_attenuation_db_per_km(Double rain_attenuation_db_per_km) { this.rain_attenuation_db_per_km = rain_attenuation_db_per_km; }

    public Double getFree_space_path_loss_db() { return free_space_path_loss_db; }
    public void setFree_space_path_loss_db(Double free_space_path_loss_db) { this.free_space_path_loss_db = free_space_path_loss_db; }

    public Double getTotal_path_loss_db() { return total_path_loss_db; }
    public void setTotal_path_loss_db(Double total_path_loss_db) { this.total_path_loss_db = total_path_loss_db; }

    public Double getReceived_power_dbm() { return received_power_dbm; }
    public void setReceived_power_dbm(Double received_power_dbm) { this.received_power_dbm = received_power_dbm; }

    public List<Double> getBer_at_distances() { return ber_at_distances; }
    public void setBer_at_distances(List<Double> ber_at_distances) { this.ber_at_distances = ber_at_distances; }

    public List<Double> getDistance_range_meters() { return distance_range_meters; }
    public void setDistance_range_meters(List<Double> distance_range_meters) { this.distance_range_meters = distance_range_meters; }

    public List<Map<String, Double>> getAbsorption_spectrum() { return absorption_spectrum; }
    public void setAbsorption_spectrum(List<Map<String, Double>> absorption_spectrum) { this.absorption_spectrum = absorption_spectrum; }

    public Double getMax_viable_range_meters() { return max_viable_range_meters; }
    public void setMax_viable_range_meters(Double max_viable_range_meters) { this.max_viable_range_meters = max_viable_range_meters; }
}

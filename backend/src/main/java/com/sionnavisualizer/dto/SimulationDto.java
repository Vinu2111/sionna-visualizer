package com.sionnavisualizer.dto;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) that mirrors the JSON structure returned by the
 * Python FastAPI bridge at GET http://localhost:8001/simulate/demo
 * 
 * This object is used to deserialize (parse) the Python response,
 * and also serialized back to JSON when the Java API responds to Angular.
 * 
 * A DTO is just a simple data carrier — no business logic, just fields.
 */
public class SimulationDto {

    // List of Signal-to-Noise Ratio values tested — e.g. [0, 2, 4, ... 20]
    private List<Double> snr_db;

    // List of Bit Error Rates at each SNR point — the "waterfall curve" data
    private List<Double> ber;

    // Flexible map containing simulation parameters like num_ofdm_symbols, fft_size, etc.
    private Map<String, Object> metadata;

    // ISO format timestamp from when the Python simulation completed
    private String timestamp;

    // --- Getters and Setters ---
    // Jackson (Spring's JSON library) uses these to convert JSON <-> Java objects

    public List<Double> getSnr_db() { return snr_db; }
    public void setSnr_db(List<Double> snr_db) { this.snr_db = snr_db; }

    public List<Double> getBer() { return ber; }
    public void setBer(List<Double> ber) { this.ber = ber; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

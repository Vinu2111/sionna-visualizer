package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Data Transfer Object that mirrors the JSON response from the Python FastAPI bridge.
 *
 * The Python bridge now returns two BER curves:
 *   - ber_theoretical : analytically computed (closed-form Q/erfc formula)
 *   - ber_simulated   : Monte-Carlo computed with numpy AWGN noise injection
 *
 * This DTO is used both to deserialize the Python response and to serialize
 * the final JSON back to the Angular frontend.
 */
public class SimulationDto {
    private Long id;


    /** SNR values tested, in dB (e.g. [-5.0, -3.33, ..., 20.0]) */
    private List<Double> snr_db;

    /** Theoretical BER at each SNR point (closed-form formula) */
    private List<Double> ber_theoretical;

    /** Monte-Carlo simulated BER at each SNR point */
    private List<Double> ber_simulated;

    /** Modulation scheme name, e.g. "QPSK", "16QAM" */
    @NotBlank
    private String modulation;

    /** Code rate, e.g. 0.5 */
    @NotNull
    @Min(0)
    private Double code_rate;

    /** Wall-clock time the Python simulation took in milliseconds */
    @NotNull
    @Min(0)
    private Integer simulation_time_ms;

    /** Total number of bits processed in the Monte-Carlo run */
    @NotNull
    @Min(0)
    private Integer num_bits_simulated;

    private PerformanceDto performance;
    private List<String> colors;
    @NotBlank
    private String colormap_used;

    // ─── Getters & Setters ──────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<Double> getSnr_db() { return snr_db; }
    public void setSnr_db(List<Double> snr_db) { this.snr_db = snr_db; }

    public List<Double> getBer_theoretical() { return ber_theoretical; }
    public void setBer_theoretical(List<Double> ber_theoretical) { this.ber_theoretical = ber_theoretical; }

    public List<Double> getBer_simulated() { return ber_simulated; }
    public void setBer_simulated(List<Double> ber_simulated) { this.ber_simulated = ber_simulated; }

    public String getModulation() { return modulation; }
    public void setModulation(String modulation) { this.modulation = modulation; }

    public Double getCode_rate() { return code_rate; }
    public void setCode_rate(Double code_rate) { this.code_rate = code_rate; }

    public Integer getSimulation_time_ms() { return simulation_time_ms; }
    public void setSimulation_time_ms(Integer simulation_time_ms) { this.simulation_time_ms = simulation_time_ms; }

    public Integer getNum_bits_simulated() { return num_bits_simulated; }
    public void setNum_bits_simulated(Integer num_bits_simulated) { this.num_bits_simulated = num_bits_simulated; }

    public PerformanceDto getPerformance() { return performance; }
    public void setPerformance(PerformanceDto performance) { this.performance = performance; }

    public List<String> getColors() { return colors; }
    public void setColors(List<String> colors) { this.colors = colors; }

    public String getColormap_used() { return colormap_used; }
    public void setColormap_used(String colormap_used) { this.colormap_used = colormap_used; }
}

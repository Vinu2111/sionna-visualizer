package com.sionnavisualizer.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Request body sent from Java → Python bridge when triggering a new simulation.
 * Field names use snake_case to match the Pydantic model in the Python bridge exactly.
 */
public class SimulationRequestDto {

    @Min(2) @Max(64)
    private int modulation_order = 4;       // QPSK default
    
    @DecimalMin("0.1") @DecimalMax("1.0")
    private double code_rate = 0.5;
    
    private int num_bits_per_symbol = 2;    // QPSK = 2 bits/symbol
    
    @DecimalMin("-20.0") @DecimalMax("0.0")
    private double snr_min = -5.0;
    
    @DecimalMin("0.0") @DecimalMax("40.0")
    private double snr_max = 20.0;
    
    @Min(10) @Max(100)
    private int snr_steps = 25;

    // ─── Constructors ────────────────────────────────────────────────────────

    /** Default constructor — produces QPSK rate-1/2 parameters */
    public SimulationRequestDto() {}

    /** Full constructor for programmatic use */
    public SimulationRequestDto(int modulation_order, double code_rate,
                                 int num_bits_per_symbol,
                                 double snr_min, double snr_max, int snr_steps) {
        this.modulation_order = modulation_order;
        this.code_rate = code_rate;
        this.num_bits_per_symbol = num_bits_per_symbol;
        this.snr_min = snr_min;
        this.snr_max = snr_max;
        this.snr_steps = snr_steps;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public int getModulation_order() { return modulation_order; }
    public void setModulation_order(int modulation_order) { this.modulation_order = modulation_order; }

    public double getCode_rate() { return code_rate; }
    public void setCode_rate(double code_rate) { this.code_rate = code_rate; }

    public int getNum_bits_per_symbol() { return num_bits_per_symbol; }
    public void setNum_bits_per_symbol(int num_bits_per_symbol) { this.num_bits_per_symbol = num_bits_per_symbol; }

    public double getSnr_min() { return snr_min; }
    public void setSnr_min(double snr_min) { this.snr_min = snr_min; }

    public double getSnr_max() { return snr_max; }
    public void setSnr_max(double snr_max) { this.snr_max = snr_max; }

    public int getSnr_steps() { return snr_steps; }
    public void setSnr_steps(int snr_steps) { this.snr_steps = snr_steps; }
}

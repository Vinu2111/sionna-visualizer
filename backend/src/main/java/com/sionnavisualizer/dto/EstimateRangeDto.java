package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class EstimateRangeDto {
    @NotNull
    @Min(0)
    private int min_ms;
    @NotNull
    @Min(0)
    private int max_ms;
}

package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

public class UpdateKpiValueRequest {
    @NotNull
    @Min(0)
    private Double actualValue;

    public Double getActualValue() { return actualValue; }
    public void setActualValue(Double actualValue) { this.actualValue = actualValue; }
}

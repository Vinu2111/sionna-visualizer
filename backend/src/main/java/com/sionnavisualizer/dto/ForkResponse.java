package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class ForkResponse {
    @NotNull
    @Min(0)
    private Long newSimulationId;
    @NotBlank
    private String redirectUrl;
    @NotBlank
    private String message;

    public Long getNewSimulationId() { return newSimulationId; }
    public void setNewSimulationId(Long newSimulationId) { this.newSimulationId = newSimulationId; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

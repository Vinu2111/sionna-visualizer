package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class SaveScenarioRequest {
    @NotBlank
    private String name;
    private ThzRequest params;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ThzRequest getParams() { return params; }
    public void setParams(ThzRequest params) { this.params = params; }
}

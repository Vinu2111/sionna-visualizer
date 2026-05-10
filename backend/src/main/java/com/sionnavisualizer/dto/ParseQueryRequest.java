package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class ParseQueryRequest {
    @NotBlank
    private String query;

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
}

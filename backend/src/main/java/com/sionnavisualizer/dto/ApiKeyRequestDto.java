package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ApiKeyRequestDto {
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 255, message = "Description is too long")
    @NotBlank
    private String description;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

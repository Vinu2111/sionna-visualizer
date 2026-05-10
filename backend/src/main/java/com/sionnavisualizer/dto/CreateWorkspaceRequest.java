package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class CreateWorkspaceRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private String institution;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
}

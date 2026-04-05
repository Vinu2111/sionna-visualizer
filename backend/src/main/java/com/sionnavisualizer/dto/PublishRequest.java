package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PublishRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String visibility;
    private List<String> customTags;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public List<String> getCustomTags() { return customTags; }
    public void setCustomTags(List<String> customTags) { this.customTags = customTags; }
}

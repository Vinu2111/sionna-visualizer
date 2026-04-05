package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class AddTagRequest {
    @NotBlank
    private String tag;
    
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}

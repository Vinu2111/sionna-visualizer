package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class TagCountResponse {
    @NotBlank
    private String tag;
    @NotNull
    @Min(0)
    private Long count;

    public TagCountResponse(String tag, Long count) {
        this.tag = tag;
        this.count = count;
    }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}

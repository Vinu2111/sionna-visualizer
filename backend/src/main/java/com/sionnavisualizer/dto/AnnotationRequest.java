package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class AnnotationRequest {
    @NotNull
    @Min(0)
    private Double snrPoint;
    @NotNull
    @Min(0)
    private Double berPoint;
    @NotBlank
    private String text;

    public Double getSnrPoint() { return snrPoint; }
    public void setSnrPoint(Double snrPoint) { this.snrPoint = snrPoint; }

    public Double getBerPoint() { return berPoint; }
    public void setBerPoint(Double berPoint) { this.berPoint = berPoint; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}

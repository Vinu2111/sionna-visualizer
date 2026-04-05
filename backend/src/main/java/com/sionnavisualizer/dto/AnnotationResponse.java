package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class AnnotationResponse {
    @NotNull
    @Min(0)
    private Long annotationId;
    @NotBlank
    private String authorName;
    @NotNull
    @Min(0)
    private Double snrPoint;
    @NotNull
    @Min(0)
    private Double berPoint;
    @NotBlank
    private String text;
    @NotNull
    @Min(0)
    private Integer pinNumber;
    @NotBlank
    private String createdAt;

    public Long getAnnotationId() { return annotationId; }
    public void setAnnotationId(Long annotationId) { this.annotationId = annotationId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public Double getSnrPoint() { return snrPoint; }
    public void setSnrPoint(Double snrPoint) { this.snrPoint = snrPoint; }

    public Double getBerPoint() { return berPoint; }
    public void setBerPoint(Double berPoint) { this.berPoint = berPoint; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Integer getPinNumber() { return pinNumber; }
    public void setPinNumber(Integer pinNumber) { this.pinNumber = pinNumber; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

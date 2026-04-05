package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

public class UpdateTrlRequest {
    @NotNull
    @Min(0)
    private Integer trlLevel;

    public Integer getTrlLevel() { return trlLevel; }
    public void setTrlLevel(Integer trlLevel) { this.trlLevel = trlLevel; }
}

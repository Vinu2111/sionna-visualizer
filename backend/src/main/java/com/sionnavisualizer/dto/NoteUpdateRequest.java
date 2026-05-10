package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class NoteUpdateRequest {
    @NotBlank
    private String note;
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

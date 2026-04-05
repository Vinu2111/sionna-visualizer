package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class GalleryPageResponse {
    private List<GalleryItemResponse> content;
    @NotNull
    @Min(0)
    private int pageNumber;
    @NotNull
    @Min(0)
    private int pageSize;
    private long totalElements;
    @NotNull
    @Min(0)
    private int totalPages;

    public List<GalleryItemResponse> getContent() { return content; }
    public void setContent(List<GalleryItemResponse> content) { this.content = content; }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}

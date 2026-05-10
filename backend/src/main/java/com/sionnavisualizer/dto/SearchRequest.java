package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class SearchRequest {
    @NotBlank
    private String query;
    @NotNull
    @Min(0)
    private Long experimentId;
    @NotBlank
    private String tags;
    private Boolean starred;

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public Long getExperimentId() { return experimentId; }
    public void setExperimentId(Long experimentId) { this.experimentId = experimentId; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Boolean getStarred() { return starred; }
    public void setStarred(Boolean starred) { this.starred = starred; }
}

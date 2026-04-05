package com.sionnavisualizer.dto;

import java.util.List;

public class BulkTagRequest {
    private List<Long> simulationIds;
    private List<String> tags;

    public List<Long> getSimulationIds() { return simulationIds; }
    public void setSimulationIds(List<Long> simulationIds) { this.simulationIds = simulationIds; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}

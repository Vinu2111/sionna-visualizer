package com.sionnavisualizer.dto;

import java.util.List;

public class PathLossResultDto {
    private List<PathDto> paths;
    private PathLossSummaryDto summary;
    private PerformanceDto performance;
    private List<String> colors;
    private String colormap_used;

    public PathLossResultDto() {}

    public List<PathDto> getPaths() { return paths; }
    public void setPaths(List<PathDto> paths) { this.paths = paths; }

    public PathLossSummaryDto getSummary() { return summary; }
    public void setSummary(PathLossSummaryDto summary) { this.summary = summary; }

    public PerformanceDto getPerformance() { return performance; }
    public void setPerformance(PerformanceDto performance) { this.performance = performance; }

    public List<String> getColors() { return colors; }
    public void setColors(List<String> colors) { this.colors = colors; }

    public String getColormap_used() { return colormap_used; }
    public void setColormap_used(String colormap_used) { this.colormap_used = colormap_used; }
}

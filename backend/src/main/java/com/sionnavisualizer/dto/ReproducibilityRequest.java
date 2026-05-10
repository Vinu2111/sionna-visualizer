package com.sionnavisualizer.dto;

import jakarta.validation.constraints.*;

public class ReproducibilityRequest {

    @NotNull

    @Min(0)
    private Long simulationId;
    private boolean includeRawBerData;
    private boolean includeBeamPatternData;
    private boolean anonymizeForBlindReview;

    // Clean generated getters & setters linking to JSON boolean conversions natively
    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public boolean isIncludeRawBerData() { return includeRawBerData; }
    public void setIncludeRawBerData(boolean includeRawBerData) { this.includeRawBerData = includeRawBerData; }

    public boolean isIncludeBeamPatternData() { return includeBeamPatternData; }
    public void setIncludeBeamPatternData(boolean includeBeamPatternData) { this.includeBeamPatternData = includeBeamPatternData; }

    public boolean isAnonymizeForBlindReview() { return anonymizeForBlindReview; }
    public void setAnonymizeForBlindReview(boolean anonymizeForBlindReview) { this.anonymizeForBlindReview = anonymizeForBlindReview; }
}

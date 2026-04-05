package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

/**
 * Response DTO for GET /api/simulations/compare?id1=X&id2=Y
 * Contains both simulation records and metadata about whether
 * they can be overlaid on the same chart.
 */
public class ComparisonResponseDto {

    private SimulationResultDto simulation1;
    private SimulationResultDto simulation2;
    private ComparisonMetadata comparison_metadata;

    // ─── inner metadata ────────────────────────────────────────────────────
    public static class ComparisonMetadata {
        private boolean same_type;
        @NotBlank
        private String type1;
        @NotBlank
        private String type2;
        private boolean can_overlay;

        public ComparisonMetadata(String type1, String type2) {
            this.type1 = type1;
            this.type2 = type2;
            this.same_type = type1 != null && type1.equals(type2);
            // Mod-comparison type overlays don't make sense as combined charts
            this.can_overlay = same_type && !"MOD_COMPARISON".equals(type1);
        }

        public boolean isSame_type()   { return same_type; }
        public String  getType1()      { return type1; }
        public String  getType2()      { return type2; }
        public boolean isCan_overlay() { return can_overlay; }
    }

    // ─── Getters/Setters ────────────────────────────────────────────────────
    public SimulationResultDto getSimulation1()               { return simulation1; }
    public void setSimulation1(SimulationResultDto s)         { this.simulation1 = s; }

    public SimulationResultDto getSimulation2()               { return simulation2; }
    public void setSimulation2(SimulationResultDto s)         { this.simulation2 = s; }

    public ComparisonMetadata getComparison_metadata()        { return comparison_metadata; }
    public void setComparison_metadata(ComparisonMetadata m)  { this.comparison_metadata = m; }
}

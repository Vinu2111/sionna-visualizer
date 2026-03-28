package com.sionnavisualizer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SinrSteeringResultDto {

    @JsonProperty("steering_results")
    private List<SteeringResultEntryDto> steeringResults;

    @JsonProperty("optimal_steering")
    private OptimalSteeringDto optimalSteering;

    @JsonProperty("summary")
    private SinrSummaryDto summary;

    @JsonProperty("performance")
    private PerformanceDto performance;

    public List<SteeringResultEntryDto> getSteeringResults() { return steeringResults; }
    public void setSteeringResults(List<SteeringResultEntryDto> v) { this.steeringResults = v; }

    public OptimalSteeringDto getOptimalSteering() { return optimalSteering; }
    public void setOptimalSteering(OptimalSteeringDto v) { this.optimalSteering = v; }

    public SinrSummaryDto getSummary() { return summary; }
    public void setSummary(SinrSummaryDto v) { this.summary = v; }

    public PerformanceDto getPerformance() { return performance; }
    public void setPerformance(PerformanceDto v) { this.performance = v; }

    // ── Nested classes ────────────────────────────────────────────────────────

    public static class SteeringResultEntryDto {
        @JsonProperty("steering_angle_deg")
        private double steeringAngleDeg;

        @JsonProperty("array_gain_db")
        private double arrayGainDb;

        @JsonProperty("interference_gain_db")
        private double interferenceGainDb;

        @JsonProperty("sinr_db")
        private double sinrDb;

        @JsonProperty("efficiency_percent")
        private double efficiencyPercent;

        @JsonProperty("is_optimal")
        private boolean isOptimal;

        public double getSteeringAngleDeg() { return steeringAngleDeg; }
        public void setSteeringAngleDeg(double v) { this.steeringAngleDeg = v; }

        public double getArrayGainDb() { return arrayGainDb; }
        public void setArrayGainDb(double v) { this.arrayGainDb = v; }

        public double getInterferenceGainDb() { return interferenceGainDb; }
        public void setInterferenceGainDb(double v) { this.interferenceGainDb = v; }

        public double getSinrDb() { return sinrDb; }
        public void setSinrDb(double v) { this.sinrDb = v; }

        public double getEfficiencyPercent() { return efficiencyPercent; }
        public void setEfficiencyPercent(double v) { this.efficiencyPercent = v; }

        public boolean isOptimal() { return isOptimal; }
        public void setOptimal(boolean v) { this.isOptimal = v; }
    }

    public static class OptimalSteeringDto {
        @JsonProperty("angle_deg")
        private double angleDeg;

        @JsonProperty("sinr_db")
        private double sinrDb;

        @JsonProperty("array_gain_db")
        private double arrayGainDb;

        public double getAngleDeg() { return angleDeg; }
        public void setAngleDeg(double v) { this.angleDeg = v; }

        public double getSinrDb() { return sinrDb; }
        public void setSinrDb(double v) { this.sinrDb = v; }

        public double getArrayGainDb() { return arrayGainDb; }
        public void setArrayGainDb(double v) { this.arrayGainDb = v; }
    }

    public static class SinrSummaryDto {
        @JsonProperty("max_sinr_db")
        private double maxSinrDb;

        @JsonProperty("min_sinr_db")
        private double minSinrDb;

        @JsonProperty("sinr_range_db")
        private double sinrRangeDb;

        @JsonProperty("num_angles_above_10db")
        private int numAnglesAbove10db;

        @JsonProperty("interference_null_angle")
        private double interferenceNullAngle;

        public double getMaxSinrDb() { return maxSinrDb; }
        public void setMaxSinrDb(double v) { this.maxSinrDb = v; }

        public double getMinSinrDb() { return minSinrDb; }
        public void setMinSinrDb(double v) { this.minSinrDb = v; }

        public double getSinrRangeDb() { return sinrRangeDb; }
        public void setSinrRangeDb(double v) { this.sinrRangeDb = v; }

        public int getNumAnglesAbove10db() { return numAnglesAbove10db; }
        public void setNumAnglesAbove10db(int v) { this.numAnglesAbove10db = v; }

        public double getInterferenceNullAngle() { return interferenceNullAngle; }
        public void setInterferenceNullAngle(double v) { this.interferenceNullAngle = v; }
    }
}

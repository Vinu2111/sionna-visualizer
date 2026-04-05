package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PocDetailResponse extends PocResponse {
    private List<TrlAdvancementDto> trlHistory;
    private List<LinkedSimulationDto> linkedSimulations;
    private List<AllianceKpiResponse> kpis;
    private List<QuarterlyStatusResponse> quarterlyStatus;

    public List<TrlAdvancementDto> getTrlHistory() { return trlHistory; }
    public void setTrlHistory(List<TrlAdvancementDto> trlHistory) { this.trlHistory = trlHistory; }

    public List<LinkedSimulationDto> getLinkedSimulations() { return linkedSimulations; }
    public void setLinkedSimulations(List<LinkedSimulationDto> linkedSimulations) { this.linkedSimulations = linkedSimulations; }

    public List<AllianceKpiResponse> getKpis() { return kpis; }
    public void setKpis(List<AllianceKpiResponse> kpis) { this.kpis = kpis; }

    public List<QuarterlyStatusResponse> getQuarterlyStatus() { return quarterlyStatus; }
    public void setQuarterlyStatus(List<QuarterlyStatusResponse> quarterlyStatus) { this.quarterlyStatus = quarterlyStatus; }

    // TRL advancement entry showing when this TRL was achieved and what simulation proved it
    public static class TrlAdvancementDto {
        @NotNull
        @Min(0)
    private Integer trlLevel;
        @NotBlank
        private String achievedAt;
        @NotNull
        @Min(0)
    private Long linkedSimulationId;
        @NotBlank
        private String evidenceDescription;

        public Integer getTrlLevel() { return trlLevel; }
        public void setTrlLevel(Integer trlLevel) { this.trlLevel = trlLevel; }

        public String getAchievedAt() { return achievedAt; }
        public void setAchievedAt(String achievedAt) { this.achievedAt = achievedAt; }

        public Long getLinkedSimulationId() { return linkedSimulationId; }
        public void setLinkedSimulationId(Long linkedSimulationId) { this.linkedSimulationId = linkedSimulationId; }

        public String getEvidenceDescription() { return evidenceDescription; }
        public void setEvidenceDescription(String evidenceDescription) { this.evidenceDescription = evidenceDescription; }
    }

    // A simulation linked as evidence for TRL progress in a specific PoC
    public static class LinkedSimulationDto {
        @NotNull
        @Min(0)
    private Long simulationId;
        @NotNull
        @Min(0)
    private Integer trlEvidenceFor;
        @NotBlank
        private String linkedAt;
        @NotBlank
        private String modulation;
        @NotNull
        @Min(0)
    private Double berAt20db;

        public Long getSimulationId() { return simulationId; }
        public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

        public Integer getTrlEvidenceFor() { return trlEvidenceFor; }
        public void setTrlEvidenceFor(Integer trlEvidenceFor) { this.trlEvidenceFor = trlEvidenceFor; }

        public String getLinkedAt() { return linkedAt; }
        public void setLinkedAt(String linkedAt) { this.linkedAt = linkedAt; }

        public String getModulation() { return modulation; }
        public void setModulation(String modulation) { this.modulation = modulation; }

        public Double getBerAt20db() { return berAt20db; }
        public void setBerAt20db(Double berAt20db) { this.berAt20db = berAt20db; }
    }
}

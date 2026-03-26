package com.sionnavisualizer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Flat DTO representation of a SimulationResult entity for the comparison API.
 * Exposes all fields needed by the Angular compare page.
 */
public class SimulationResultDto {

    private Long id;
    private String simulationType;
    private String modulationType;
    private BigDecimal codeRate;
    private BigDecimal snrMin;
    private BigDecimal snrMax;
    private String snrDb;
    private String berTheoretical;
    private String berSimulated;

    // Beam pattern fields
    private String beamAngles;
    private String beamPatternDb;
    private BigDecimal steeringAngle;
    private Integer numAntennas;
    private BigDecimal frequencyGhz;
    private BigDecimal mainLobeWidth;
    private BigDecimal sideLobeLevel;

    // Mod comparison fields
    private String bpskBer;
    private String qpskBer;
    private String qam16Ber;
    private String qam64Ber;
    private String crossoverPoints;

    private String hardwareUsed;
    private Integer simulationTimeMs;
    private String shareToken;
    private Boolean isPublic;
    private LocalDateTime createdAt;

    // Performance fields
    private Long durationMs;
    private String computeType;
    private Double memoryMb;
    private String sionnaVersion;

    // ─── Getters & Setters ──────────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSimulationType() { return simulationType; }
    public void setSimulationType(String simulationType) { this.simulationType = simulationType; }

    public String getModulationType() { return modulationType; }
    public void setModulationType(String modulationType) { this.modulationType = modulationType; }

    public BigDecimal getCodeRate() { return codeRate; }
    public void setCodeRate(BigDecimal codeRate) { this.codeRate = codeRate; }

    public BigDecimal getSnrMin() { return snrMin; }
    public void setSnrMin(BigDecimal snrMin) { this.snrMin = snrMin; }

    public BigDecimal getSnrMax() { return snrMax; }
    public void setSnrMax(BigDecimal snrMax) { this.snrMax = snrMax; }

    public String getSnrDb() { return snrDb; }
    public void setSnrDb(String snrDb) { this.snrDb = snrDb; }

    public String getBerTheoretical() { return berTheoretical; }
    public void setBerTheoretical(String berTheoretical) { this.berTheoretical = berTheoretical; }

    public String getBerSimulated() { return berSimulated; }
    public void setBerSimulated(String berSimulated) { this.berSimulated = berSimulated; }

    public String getBeamAngles() { return beamAngles; }
    public void setBeamAngles(String beamAngles) { this.beamAngles = beamAngles; }

    public String getBeamPatternDb() { return beamPatternDb; }
    public void setBeamPatternDb(String beamPatternDb) { this.beamPatternDb = beamPatternDb; }

    public BigDecimal getSteeringAngle() { return steeringAngle; }
    public void setSteeringAngle(BigDecimal steeringAngle) { this.steeringAngle = steeringAngle; }

    public Integer getNumAntennas() { return numAntennas; }
    public void setNumAntennas(Integer numAntennas) { this.numAntennas = numAntennas; }

    public BigDecimal getFrequencyGhz() { return frequencyGhz; }
    public void setFrequencyGhz(BigDecimal frequencyGhz) { this.frequencyGhz = frequencyGhz; }

    public BigDecimal getMainLobeWidth() { return mainLobeWidth; }
    public void setMainLobeWidth(BigDecimal mainLobeWidth) { this.mainLobeWidth = mainLobeWidth; }

    public BigDecimal getSideLobeLevel() { return sideLobeLevel; }
    public void setSideLobeLevel(BigDecimal sideLobeLevel) { this.sideLobeLevel = sideLobeLevel; }

    public String getBpskBer() { return bpskBer; }
    public void setBpskBer(String bpskBer) { this.bpskBer = bpskBer; }

    public String getQpskBer() { return qpskBer; }
    public void setQpskBer(String qpskBer) { this.qpskBer = qpskBer; }

    public String getQam16Ber() { return qam16Ber; }
    public void setQam16Ber(String qam16Ber) { this.qam16Ber = qam16Ber; }

    public String getQam64Ber() { return qam64Ber; }
    public void setQam64Ber(String qam64Ber) { this.qam64Ber = qam64Ber; }

    public String getCrossoverPoints() { return crossoverPoints; }
    public void setCrossoverPoints(String crossoverPoints) { this.crossoverPoints = crossoverPoints; }

    public String getHardwareUsed() { return hardwareUsed; }
    public void setHardwareUsed(String hardwareUsed) { this.hardwareUsed = hardwareUsed; }

    public Integer getSimulationTimeMs() { return simulationTimeMs; }
    public void setSimulationTimeMs(Integer simulationTimeMs) { this.simulationTimeMs = simulationTimeMs; }

    public String getShareToken() { return shareToken; }
    public void setShareToken(String shareToken) { this.shareToken = shareToken; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

    public String getComputeType() { return computeType; }
    public void setComputeType(String computeType) { this.computeType = computeType; }

    public Double getMemoryMb() { return memoryMb; }
    public void setMemoryMb(Double memoryMb) { this.memoryMb = memoryMb; }

    public String getSionnaVersion() { return sionnaVersion; }
    public void setSionnaVersion(String sionnaVersion) { this.sionnaVersion = sionnaVersion; }

    /** Factory method — maps from JPA entity to DTO */
    public static SimulationResultDto from(com.sionnavisualizer.model.SimulationResult e) {
        SimulationResultDto d = new SimulationResultDto();
        d.setId(e.getId());
        d.setSimulationType(e.getSimulationType());
        d.setModulationType(e.getModulationType());
        d.setCodeRate(e.getCodeRate());
        d.setSnrMin(e.getSnrMin());
        d.setSnrMax(e.getSnrMax());
        d.setSnrDb(e.getSnrDb());
        d.setBerTheoretical(e.getBerTheoretical());
        d.setBerSimulated(e.getBerSimulated());
        d.setBeamAngles(e.getBeamAngles());
        d.setBeamPatternDb(e.getBeamPatternDb());
        d.setSteeringAngle(e.getSteeringAngle());
        d.setNumAntennas(e.getNumAntennas());
        d.setFrequencyGhz(e.getFrequencyGhz());
        d.setMainLobeWidth(e.getMainLobeWidth());
        d.setSideLobeLevel(e.getSideLobeLevel());
        d.setBpskBer(e.getBpskBer());
        d.setQpskBer(e.getQpskBer());
        d.setQam16Ber(e.getQam16Ber());
        d.setQam64Ber(e.getQam64Ber());
        d.setCrossoverPoints(e.getCrossoverPoints());
        d.setHardwareUsed(e.getHardwareUsed());
        d.setSimulationTimeMs(e.getSimulationTimeMs());
        d.setShareToken(e.getShareToken());
        d.setIsPublic(e.getIsPublic());
        d.setCreatedAt(e.getCreatedAt());

        d.setDurationMs(e.getDurationMs());
        d.setComputeType(e.getComputeType());
        d.setMemoryMb(e.getMemoryMb());
        d.setSionnaVersion(e.getSionnaVersion());

        return d;
    }
}

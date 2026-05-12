package com.sionnavisualizer.dto;

public class SdkTrackResponse {
    private boolean success;
    private Long simulationId;
    private String shareableUrl;
    private String message;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public String getShareableUrl() { return shareableUrl; }
    public void setShareableUrl(String shareableUrl) { this.shareableUrl = shareableUrl; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

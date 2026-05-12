package com.sionnavisualizer.dto;

import java.util.List;
import java.util.Map;

public class SdkTrackRequest {
    private String simulationType;
    private String title;
    private List<String> tags;
    private BerDataDto berData;
    private Map<String, Object> rawData;
    private String sdkVersion;
    private String sdkLanguage;

    public String getSimulationType() { return simulationType; }
    public void setSimulationType(String simulationType) { this.simulationType = simulationType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public BerDataDto getBerData() { return berData; }
    public void setBerData(BerDataDto berData) { this.berData = berData; }

    public Map<String, Object> getRawData() { return rawData; }
    public void setRawData(Map<String, Object> rawData) { this.rawData = rawData; }

    public String getSdkVersion() { return sdkVersion; }
    public void setSdkVersion(String sdkVersion) { this.sdkVersion = sdkVersion; }

    public String getSdkLanguage() { return sdkLanguage; }
    public void setSdkLanguage(String sdkLanguage) { this.sdkLanguage = sdkLanguage; }
}

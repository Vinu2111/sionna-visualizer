package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nl_parse_records")
public class NlParseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "query_text", length = 500, nullable = false)
    private String queryText;

    // Storing the full Claude response as JSON so we can replay without re-calling the API
    @Column(name = "extracted_params_json", columnDefinition = "TEXT")
    private String extractedParamsJson;

    @Column(length = 10)
    private String confidence;

    @Column(name = "simulation_type", length = 30)
    private String simulationType;

    @Column(name = "parsed_at", insertable = false, updatable = false)
    private LocalDateTime parsedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getQueryText() { return queryText; }
    public void setQueryText(String queryText) { this.queryText = queryText; }
    public String getExtractedParamsJson() { return extractedParamsJson; }
    public void setExtractedParamsJson(String extractedParamsJson) { this.extractedParamsJson = extractedParamsJson; }
    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }
    public String getSimulationType() { return simulationType; }
    public void setSimulationType(String simulationType) { this.simulationType = simulationType; }
    public LocalDateTime getParsedAt() { return parsedAt; }
    public void setParsedAt(LocalDateTime parsedAt) { this.parsedAt = parsedAt; }
}

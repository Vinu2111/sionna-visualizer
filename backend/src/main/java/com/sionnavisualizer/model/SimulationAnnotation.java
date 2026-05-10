package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulation_annotations")
public class SimulationAnnotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simulation_id", nullable = false)
    private Long simulationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "author_name", length = 100)
    private String authorName;

    @Column(name = "snr_point")
    private Double snrPoint;

    @Column(name = "ber_point")
    private Double berPoint;

    @Column(name = "annotation_text", nullable = false, length = 500)
    private String annotationText;

    @Column(name = "pin_number")
    private Integer pinNumber;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSimulationId() { return simulationId; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public Double getSnrPoint() { return snrPoint; }
    public void setSnrPoint(Double snrPoint) { this.snrPoint = snrPoint; }

    public Double getBerPoint() { return berPoint; }
    public void setBerPoint(Double berPoint) { this.berPoint = berPoint; }

    public String getAnnotationText() { return annotationText; }
    public void setAnnotationText(String annotationText) { this.annotationText = annotationText; }

    public Integer getPinNumber() { return pinNumber; }
    public void setPinNumber(Integer pinNumber) { this.pinNumber = pinNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

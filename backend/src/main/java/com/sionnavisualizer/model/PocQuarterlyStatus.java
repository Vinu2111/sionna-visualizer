package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "poc_quarterly_status")
public class PocQuarterlyStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "poc_id", nullable = false)
    private Long pocId;

    @Column(length = 5, nullable = false)
    private String quarter;

    @Column(name = "submission_year", nullable = false)
    private Integer year;

    @Column(length = 20)
    private String status = "NOT_DUE";

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPocId() { return pocId; }
    public void setPocId(Long pocId) { this.pocId = pocId; }
    public String getQuarter() { return quarter; }
    public void setQuarter(String quarter) { this.quarter = quarter; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}

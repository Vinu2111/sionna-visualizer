package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.AnomalyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnomalyReportRepository extends JpaRepository<AnomalyReport, Long> {
    Optional<AnomalyReport> findBySimulationId(Long simulationId);
    List<AnomalyReport> findByUserIdOrderByAnalyzedAtDesc(Long userId);
}

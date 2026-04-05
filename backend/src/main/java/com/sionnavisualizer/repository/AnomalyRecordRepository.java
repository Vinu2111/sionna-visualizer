package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.AnomalyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnomalyRecordRepository extends JpaRepository<AnomalyRecord, Long> {
    List<AnomalyRecord> findByReportId(Long reportId);
    List<AnomalyRecord> findBySimulationId(Long simulationId);
    List<AnomalyRecord> findBySeverity(String severity);
}

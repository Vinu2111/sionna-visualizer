package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.SigmfImportRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SigmfImportRepository extends JpaRepository<SigmfImportRecord, Long> {
    List<SigmfImportRecord> findByUserId(Long userId);
    List<SigmfImportRecord> findBySimulationId(Long simulationId);
}

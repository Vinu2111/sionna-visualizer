package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.LatexExportRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LatexExportRepository extends JpaRepository<LatexExportRecord, Long> {
    
    // Custom method to track all latex exports generated for a specific simulation run
    List<LatexExportRecord> findBySimulationId(Long simulationId);
}

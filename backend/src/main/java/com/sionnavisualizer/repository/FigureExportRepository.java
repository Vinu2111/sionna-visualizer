package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.FigureExport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FigureExportRepository extends JpaRepository<FigureExport, Long> {
    // Finds all exports associated with a specific simulation
    List<FigureExport> findBySimulationId(Long simulationId);
}

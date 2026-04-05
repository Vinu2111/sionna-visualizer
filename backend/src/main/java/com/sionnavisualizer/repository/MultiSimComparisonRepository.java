package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.MultiSimComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultiSimComparisonRepository extends JpaRepository<MultiSimComparison, Long> {
    List<MultiSimComparison> findByUserId(Long userId);
    List<MultiSimComparison> findBySionnaSimulationId(Long simulationId);
}

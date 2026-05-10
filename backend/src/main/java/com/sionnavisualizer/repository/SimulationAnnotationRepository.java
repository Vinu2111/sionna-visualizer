package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.SimulationAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationAnnotationRepository extends JpaRepository<SimulationAnnotation, Long> {
    List<SimulationAnnotation> findBySimulationId(Long simulationId);
    
    @Query(value = "SELECT COALESCE(MAX(pin_number), 0) FROM simulation_annotations WHERE simulation_id = :simId", nativeQuery = true)
    Integer findMaxPinNumber(@Param("simId") Long simId);
}

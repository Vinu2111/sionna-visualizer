package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.SimulationVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationVersionRepository extends JpaRepository<SimulationVersion, Long> {
    List<SimulationVersion> findBySimulationIdOrderByVersionNumberDesc(Long simulationId);

    @Query(value = "SELECT COALESCE(MAX(version_number), 0) FROM simulation_versions WHERE simulation_id = :simId", nativeQuery = true)
    Integer findMaxVersionNumber(@Param("simId") Long simId);
}

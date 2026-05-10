package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.SimulationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for database access on the SimulationResult entity.
 * Spring Data JPA automatically implements all the standard CRUD methods —
 * save(), findById(), findAll(), delete() — at runtime. No SQL needed.
 */
@Repository
public interface SimulationResultRepository extends JpaRepository<SimulationResult, Long> {

    /**
     * Custom query method: returns all simulation results ordered newest first.
     * Spring Data JPA generates the correct SQL from this method name automatically:
     * SELECT * FROM simulation_results ORDER BY created_at DESC
     */
    List<SimulationResult> findAllByOrderByCreatedAtDesc();

    // Resolves identical simulation explicitly securely mathematically internally via Token mapped URL
    java.util.Optional<SimulationResult> findByShareToken(String shareToken);
}

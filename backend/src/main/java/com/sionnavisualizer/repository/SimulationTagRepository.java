package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.SimulationTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SimulationTagRepository extends JpaRepository<SimulationTag, Long> {
    List<SimulationTag> findBySimulationId(Long simulationId);
    Optional<SimulationTag> findByUserIdAndTag(Long userId, String tag);
    void deleteBySimulationIdAndTag(Long simulationId, String tag);
    
    @Query(value = "SELECT tag, COUNT(*) as count FROM simulation_tags WHERE user_id = :userId GROUP BY tag ORDER BY count DESC", nativeQuery = true)
    List<Object[]> countByUserIdGroupByTag(@Param("userId") Long userId);
}

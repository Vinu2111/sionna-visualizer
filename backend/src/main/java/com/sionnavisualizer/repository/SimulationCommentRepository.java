package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.SimulationComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationCommentRepository extends JpaRepository<SimulationComment, Long> {
    List<SimulationComment> findBySimulationIdOrderByIdAsc(Long simulationId);
    List<SimulationComment> findBySimulationIdAndParentCommentIdIsNullOrderByIdAsc(Long simulationId);
}

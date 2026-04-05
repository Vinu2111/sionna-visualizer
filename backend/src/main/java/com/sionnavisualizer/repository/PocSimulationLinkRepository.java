package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.PocSimulationLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PocSimulationLinkRepository extends JpaRepository<PocSimulationLink, Long> {
    List<PocSimulationLink> findByPocId(Long pocId);
    boolean existsByPocIdAndSimulationId(Long pocId, Long simulationId);
}

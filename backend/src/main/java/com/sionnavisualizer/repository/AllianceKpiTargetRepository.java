package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.AllianceKpiTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AllianceKpiTargetRepository extends JpaRepository<AllianceKpiTarget, Long> {
    List<AllianceKpiTarget> findByPocId(Long pocId);
    List<AllianceKpiTarget> findByPocIdAndAllianceTrack(Long pocId, String allianceTrack);
}

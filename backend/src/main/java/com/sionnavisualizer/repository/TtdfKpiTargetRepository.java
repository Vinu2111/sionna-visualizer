package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.TtdfKpiTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TtdfKpiTargetRepository extends JpaRepository<TtdfKpiTarget, Long> {
    List<TtdfKpiTarget> findByMilestoneId(Long milestoneId);
    List<TtdfKpiTarget> findByMilestoneIdAndMetricType(Long milestoneId, String metricType);
}

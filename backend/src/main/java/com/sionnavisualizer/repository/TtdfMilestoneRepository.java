package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.TtdfMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TtdfMilestoneRepository extends JpaRepository<TtdfMilestone, Long> {
    List<TtdfMilestone> findByProjectId(Long projectId);
    List<TtdfMilestone> findByProjectIdAndStatus(Long projectId, String status);

    @Query(value = "SELECT m.* FROM ttdf_milestones m WHERE m.project_id = :projectId AND m.due_date < CURRENT_DATE AND m.status != 'COMPLETED'", nativeQuery = true)
    List<TtdfMilestone> findOverdueMilestones(@Param("projectId") Long projectId);
}

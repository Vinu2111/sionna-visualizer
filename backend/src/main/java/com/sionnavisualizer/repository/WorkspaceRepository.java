package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findByOwnerId(Long ownerId);

    @Query(value = "SELECT w.* FROM workspaces w JOIN workspace_members wm ON w.id = wm.workspace_id WHERE wm.user_id = :userId", nativeQuery = true)
    List<Workspace> findWorkspacesByUserId(@Param("userId") Long userId);
}

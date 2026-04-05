package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.TtdfProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TtdfProjectRepository extends JpaRepository<TtdfProject, Long> {
    Optional<TtdfProject> findByUserId(Long userId);
}

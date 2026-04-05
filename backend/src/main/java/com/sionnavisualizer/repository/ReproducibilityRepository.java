package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.ReproducibilityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReproducibilityRepository extends JpaRepository<ReproducibilityRecord, Long> {
    // Inherit standard JPA crud operations natively
}

package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.AllianceOrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AllianceOrganizationRepository extends JpaRepository<AllianceOrganizationEntity, Long> {
    Optional<AllianceOrganizationEntity> findByUserId(Long userId);
}

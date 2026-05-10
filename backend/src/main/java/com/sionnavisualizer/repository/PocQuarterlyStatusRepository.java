package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.PocQuarterlyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PocQuarterlyStatusRepository extends JpaRepository<PocQuarterlyStatus, Long> {
    List<PocQuarterlyStatus> findByPocId(Long pocId);
    Optional<PocQuarterlyStatus> findByPocIdAndQuarterAndYear(Long pocId, String quarter, Integer year);
}

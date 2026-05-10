package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.PocTrlHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PocTrlHistoryRepository extends JpaRepository<PocTrlHistory, Long> {
    List<PocTrlHistory> findByPocIdOrderByTrlLevelAsc(Long pocId);
}

package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.BharatPoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BharatPocRepository extends JpaRepository<BharatPoc, Long> {
    List<BharatPoc> findByUserId(Long userId);
    List<BharatPoc> findByUserIdAndStatus(Long userId, String status);
}

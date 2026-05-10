package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.NlParseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NlParseRepository extends JpaRepository<NlParseRecord, Long> {
    // Returns the 10 most recent parse attempts by this user, newest first
    List<NlParseRecord> findTop10ByUserIdOrderByParsedAtDesc(Long userId);
}

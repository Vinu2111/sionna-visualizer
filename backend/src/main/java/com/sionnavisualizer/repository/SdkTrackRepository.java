package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.SdkTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SdkTrackRepository extends JpaRepository<SdkTrack, Long> {
}

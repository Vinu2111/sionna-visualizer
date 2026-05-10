package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.ChannelModelResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelModelRepository extends JpaRepository<ChannelModelResult, Long> {
    
    // Locates all runs associated with a specific channel type architecture (e.g. "CDL-A")
    List<ChannelModelResult> findByChannelModel(String channelModel);
    
    // Locates all histories linked to a distinct user credential record
    List<ChannelModelResult> findByUserId(Long userId);
}

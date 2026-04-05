package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.GalleryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryItemRepository extends JpaRepository<GalleryItem, Long>, JpaSpecificationExecutor<GalleryItem> {
    List<GalleryItem> findByUserId(Long userId);
}

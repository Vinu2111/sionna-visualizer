package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.GalleryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryCommentRepository extends JpaRepository<GalleryComment, Long> {
    List<GalleryComment> findByGalleryItemIdOrderByIdDesc(Long galleryItemId);
}

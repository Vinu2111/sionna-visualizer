package com.sionnavisualizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gallery_comments")
public class GalleryComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gallery_item_id", nullable = false)
    private Long galleryItemId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "author_name", length = 100)
    private String authorName;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public GalleryComment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGalleryItemId() { return galleryItemId; }
    public void setGalleryItemId(Long galleryItemId) { this.galleryItemId = galleryItemId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

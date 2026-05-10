package com.sionnavisualizer.controller;

import jakarta.validation.Valid;

import com.sionnavisualizer.dto.ForkResponse;
import com.sionnavisualizer.dto.GalleryItemResponse;
import com.sionnavisualizer.dto.GalleryPageResponse;
import com.sionnavisualizer.dto.PublishRequest;
import com.sionnavisualizer.model.GalleryComment;
import com.sionnavisualizer.service.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping
    public ResponseEntity<GalleryPageResponse> getGallery(
            @RequestParam(required = false) String channelModel,
            @RequestParam(required = false) String modulation,
            @RequestParam(required = false) Double frequency,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Sort sort = Sort.by(Sort.Direction.DESC, "publishedAt");
        if ("mostViewed".equals(sortBy)) sort = Sort.by(Sort.Direction.DESC, "viewCount");
        if ("mostForked".equals(sortBy)) sort = Sort.by(Sort.Direction.DESC, "forkCount");

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(galleryService.getGalleryPage(channelModel, modulation, frequency, search, pageable));
    }

    @GetMapping("/{galleryId}")
    public ResponseEntity<GalleryItemResponse> getGalleryDetail(@PathVariable Long galleryId) {
        try {
            return ResponseEntity.ok(galleryService.getGalleryDetail(galleryId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/publish")
    public ResponseEntity<GalleryItemResponse> publishSimulation(
            @RequestParam Long simulationId,
            @Valid @RequestBody PublishRequest request) {
        try {
            Long userId = 1L; // Mock logged-in user
            return ResponseEntity.ok(galleryService.publishSimulation(simulationId, request, userId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{galleryId}/fork")
    public ResponseEntity<ForkResponse> forkSimulation(@PathVariable Long galleryId) {
        try {
            Long userId = 1L; // Mock logged-in user
            return ResponseEntity.ok(galleryService.forkSimulation(galleryId, userId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{galleryId}/comments")
    public ResponseEntity<GalleryComment> addComment(
            @PathVariable Long galleryId,
            @RequestParam String content) {
        try {
            Long userId = 1L; // Mock logged-in user
            return ResponseEntity.ok(galleryService.addComment(galleryId, content, userId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

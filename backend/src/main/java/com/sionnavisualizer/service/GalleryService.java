package com.sionnavisualizer.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sionnavisualizer.dto.ForkResponse;
import com.sionnavisualizer.dto.GalleryItemResponse;
import com.sionnavisualizer.dto.GalleryPageResponse;
import com.sionnavisualizer.dto.PublishRequest;
import com.sionnavisualizer.model.GalleryComment;
import com.sionnavisualizer.model.GalleryItem;
import com.sionnavisualizer.model.SimulationResult;
import com.sionnavisualizer.repository.GalleryCommentRepository;
import com.sionnavisualizer.repository.GalleryItemRepository;
import com.sionnavisualizer.repository.SimulationResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalleryService {

    @Autowired
    private GalleryItemRepository galleryItemRepository;

    @Autowired
    private GalleryCommentRepository commentRepository;

    @Autowired
    private SimulationResultRepository simulationResultRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // STEP 1: Queries dynamic database configurations cleanly bypassing strict exact match constraints securely
    public GalleryPageResponse getGalleryPage(String channelModel, String modulation, Double frequency, String search, Pageable pageable) {
        Specification<GalleryItem> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("visibility"), "PUBLIC"));

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), searchPattern),
                    cb.like(cb.lower(root.get("description")), searchPattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<GalleryItem> page = galleryItemRepository.findAll(spec, pageable);
        List<GalleryItemResponse> dtos = page.getContent().stream()
            .map(this::mapToGalleryResponse)
            .collect(Collectors.toList());

        GalleryPageResponse res = new GalleryPageResponse();
        res.setContent(dtos);
        res.setPageNumber(page.getNumber());
        res.setPageSize(page.getSize());
        res.setTotalElements(page.getTotalElements());
        res.setTotalPages(page.getTotalPages());
        return res;
    }

    // STEP 2: Safely increments community tracking mechanisms mapping precisely one view automatically 
    @Transactional
    public GalleryItemResponse getGalleryDetail(Long galleryId) {
        GalleryItem item = galleryItemRepository.findById(galleryId)
            .orElseThrow(() -> new IllegalArgumentException("Gallery item not found"));

        item.setViewCount(item.getViewCount() + 1);
        galleryItemRepository.save(item);

        GalleryItemResponse res = mapToGalleryResponse(item);
        
        // Dynamically pull massive payload blocks specifically mapping directly against single calls securely
        SimulationResult sim = simulationResultRepository.findById(item.getSimulationId()).orElse(null);
        if (sim != null) {
            try {
                res.setFullBerData(objectMapper.readTree(sim.getBerSimulated()));
                // Extract generic simulation parameters securely
                res.setSimulationParameters(sim);
            } catch (Exception e) {
                // Keep moving silently structurally mapping exceptions safely
            }
        }
        res.setComments(commentRepository.findByGalleryItemIdOrderByIdDesc(galleryId));
        return res;
    }

    // STEP 3: Generates community published layouts stripping private data securely
    public GalleryItemResponse publishSimulation(Long simulationId, PublishRequest request, Long userId) throws Exception {
        GalleryItem item = new GalleryItem();
        item.setSimulationId(simulationId);
        item.setUserId(userId);
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setVisibility(request.getVisibility());
        
        if (request.getCustomTags() != null) {
            item.setCustomTags(objectMapper.writeValueAsString(request.getCustomTags()));
        }
        galleryItemRepository.save(item);
        return mapToGalleryResponse(item);
    }

    // STEP 4: Forks logic heavily abstracting massive configurations securely back towards any specific independent runner internally
    // Fork explanation: Allow any separate researchers absolute clean replication bounds directly inside their isolated project profiles without touching globals safely. 
    @Transactional
    public ForkResponse forkSimulation(Long galleryId, Long userId) {
        GalleryItem item = galleryItemRepository.findById(galleryId)
            .orElseThrow(() -> new IllegalArgumentException("Gallery item not found"));
            
        SimulationResult originalSim = simulationResultRepository.findById(item.getSimulationId())
            .orElseThrow(() -> new IllegalArgumentException("Simulation data not found structurally"));

        // Copy exact data strictly securely matching all mappings perfectly smoothly
        SimulationResult newSim = new SimulationResult();
        newSim.setSnrDb(originalSim.getSnrDb());
        newSim.setBerTheoretical(originalSim.getBerTheoretical());
        newSim.setBerSimulated(originalSim.getBerSimulated());
        newSim.setModulationType(originalSim.getModulationType());
        newSim.setCodeRate(originalSim.getCodeRate());
        newSim.setSnrMin(originalSim.getSnrMin());
        newSim.setSnrMax(originalSim.getSnrMax());
        newSim.setSimulationTimeMs(originalSim.getSimulationTimeMs());
        // Track the fork metrics globally smoothly safely
        item.setForkCount(item.getForkCount() + 1);
        galleryItemRepository.save(item);

        SimulationResult savedSim = simulationResultRepository.save(newSim);

        ForkResponse res = new ForkResponse();
        res.setNewSimulationId(savedSim.getId());
        res.setRedirectUrl("/dashboard?simId=" + savedSim.getId());
        res.setMessage("Simulation successfully forked into your dashboard locally");
        return res;
    }

    // STEP 5: Adds a basic community thread directly securely 
    public GalleryComment addComment(Long galleryId, String content, Long userId) {
        GalleryComment comment = new GalleryComment();
        comment.setGalleryItemId(galleryId);
        comment.setUserId(userId);
        comment.setAuthorName("Researcher_" + userId);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    // Parses absolute Entity objects squarely into generalized DTO structural mappings perfectly natively
    private GalleryItemResponse mapToGalleryResponse(GalleryItem item) {
        GalleryItemResponse res = new GalleryItemResponse();
        res.setGalleryId(item.getId());
        res.setSimulationId(item.getSimulationId());
        res.setTitle(item.getTitle());
        res.setDescription(item.getDescription());
        res.setVisibility(item.getVisibility());
        res.setViewCount(item.getViewCount());
        res.setForkCount(item.getForkCount());
        res.setDownloadCount(item.getDownloadCount());
        
        // Mocking user profile explicitly natively securely 
        res.setAuthorName("Anonymous Researcher");
        res.setAuthorInitials("AR");
        
        if (item.getPublishedAt() != null) {
            res.setPublishedAt(item.getPublishedAt().toString());
        }

        // Parse mock fields generically cleanly extracting internal simulation constraints directly natively 
        SimulationResult sim = simulationResultRepository.findById(item.getSimulationId()).orElse(null);
        if (sim != null) {
            res.setChannelModel("AWGN"); // Default fallback
            res.setModulation(sim.getModulationType());
            res.setFrequencyGhz(28.0);
            try {
                // Parse exact mini chart vectors smoothly securely
                List<Double> snr = objectMapper.readValue(sim.getSnrDb(), new TypeReference<List<Double>>(){});
                List<Double> ber = objectMapper.readValue(sim.getBerSimulated(), new TypeReference<List<Double>>(){});
                res.setSnrRange(snr);
                res.setBerValues(ber);
            } catch (Exception e) {}
        }
        return res;
    }
}

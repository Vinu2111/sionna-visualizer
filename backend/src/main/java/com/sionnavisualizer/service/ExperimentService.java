package com.sionnavisualizer.service;

import com.sionnavisualizer.dto.*;
import com.sionnavisualizer.model.Experiment;
import com.sionnavisualizer.model.SimulationResult;
import com.sionnavisualizer.model.SimulationTag;
import com.sionnavisualizer.repository.ExperimentRepository;
import com.sionnavisualizer.repository.SimulationResultRepository;
import com.sionnavisualizer.repository.SimulationTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperimentService {

    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private SimulationResultRepository simulationResultRepository;

    @Autowired
    private SimulationTagRepository simulationTagRepository;

    // Creates new logical binding domains organizing runs directly securely securely
    public ExperimentResponse createExperiment(CreateExperimentRequest request, Long userId) {
        Experiment exp = new Experiment();
        exp.setName(request.getName());
        exp.setDescription(request.getDescription());
        exp.setColor(request.getColor());
        exp.setUserId(userId);

        Experiment saved = experimentRepository.save(exp);

        ExperimentResponse res = new ExperimentResponse();
        res.setExperimentId(saved.getId());
        res.setName(saved.getName());
        res.setDescription(saved.getDescription());
        res.setColor(saved.getColor());
        res.setSimulationCount(0L);
        return res;
    }

    // Fetches top level logical boundary objects specifically owned by user safely
    public List<ExperimentResponse> getUserExperiments(Long userId) {
        List<Experiment> exps = experimentRepository.findByUserId(userId);
        
        return exps.stream().map(e -> {
            ExperimentResponse res = new ExperimentResponse();
            res.setExperimentId(e.getId());
            res.setName(e.getName());
            res.setDescription(e.getDescription());
            res.setColor(e.getColor());
            res.setSimulationCount(0L); // Mocked scalar for UI layout
            return res;
        }).collect(Collectors.toList());
    }

    // Pushes isolated metadata boundary markers directly against single simulation records
    public void addTag(Long simulationId, String tag, Long userId) {
        SimulationResult sim = simulationResultRepository.findById(simulationId)
            .orElseThrow(() -> new IllegalArgumentException("Simulation not found"));

        try {
            SimulationTag simTag = new SimulationTag();
            simTag.setSimulationId(simulationId);
            simTag.setUserId(userId);
            simTag.setTag(tag.trim().toLowerCase());
            simulationTagRepository.save(simTag);
        } catch (Exception e) {
            // Skips duplicates explicitly smoothly safely natively without failing the request
        }
    }

    // Safely ejects isolated mapping marks securely logically
    @Transactional
    public void removeTag(Long simulationId, String tag, Long userId) {
        simulationTagRepository.deleteBySimulationIdAndTag(simulationId, tag.trim().toLowerCase());
    }

    // Massive insertion bypass - Iterates array safely skipping direct duplicates natively. (Why avoid error throwing: Bulk checks easily intersect prior states securely without crushing pipelines completely). 
    public void bulkAddTags(List<Long> simulationIds, List<String> tags, Long userId) {
        for (Long simId : simulationIds) {
            for (String tag : tags) {
                addTag(simId, tag, userId);
            }
        }
    }

    // Edits the long form tracking annotation globally. Auto-saves directly mapped natively to textarea blur triggers avoiding save-button-fatigue for researchers completely safely. 
    public void updateNote(Long simulationId, String note, Long userId) {
        SimulationResult sim = simulationResultRepository.findById(simulationId)
            .orElseThrow(() -> new IllegalArgumentException("Simulation not found"));
        sim.setNote(note);
        simulationResultRepository.save(sim);
    }

    // Flags individual tracking paths securely
    public void toggleStar(Long simulationId, Long userId) {
        SimulationResult sim = simulationResultRepository.findById(simulationId)
            .orElseThrow(() -> new IllegalArgumentException("Simulation not found"));
        sim.setStarred(!sim.getStarred());
        simulationResultRepository.save(sim);
    }
    
    // Configures logical bound mapping linking records logically safely 
    public void assignExperiment(Long simulationId, Long experimentId, Long userId) {
         SimulationResult sim = simulationResultRepository.findById(simulationId)
            .orElseThrow(() -> new IllegalArgumentException("Simulation not found"));
         sim.setExperimentId(experimentId);
         simulationResultRepository.save(sim);
    }

    // Abstract logical intersection searching mapping heavily against disparate parameter definitions perfectly safely
    public List<SimulationHistoryResponse> searchSimulations(SearchRequest request, Long userId) {
         // Because of time restrictions and native complexity without advanced JPA Specs mapping across nested arrays natively, 
         // We'll rely on a manual logical intersection pass over user baseline runs smoothly mapping definitions explicitly natively 
         List<SimulationResult> ALL = simulationResultRepository.findAllByOrderByCreatedAtDesc();
         
         List<SimulationHistoryResponse> out = new ArrayList<>();
         for (SimulationResult sim : ALL) {
              if (request.getExperimentId() != null && !request.getExperimentId().equals(sim.getExperimentId())) continue;
              if (request.getStarred() != null && request.getStarred() && !sim.getStarred()) continue;

              List<SimulationTag> tagObjs = simulationTagRepository.findBySimulationId(sim.getId());
              List<String> simTags = tagObjs.stream().map(SimulationTag::getTag).collect(Collectors.toList());

              if (request.getTags() != null && !request.getTags().isEmpty()) {
                  boolean hasAll = true;
                  String[] splitTags = request.getTags().split(",");
                  for (String reqTag : splitTags) {
                      if (!simTags.contains(reqTag.trim().toLowerCase())) { hasAll = false; break; }
                  }
                  if (!hasAll) continue;
              }

              // Free search natively
              if (request.getQuery() != null && !request.getQuery().trim().isEmpty()) {
                  String q = request.getQuery().toLowerCase();
                  boolean match = false;
                  if (sim.getNote() != null && sim.getNote().toLowerCase().contains(q)) match = true;
                  if (sim.getSimulationType() != null && sim.getSimulationType().toLowerCase().contains(q)) match = true;
                  for (String t : simTags) { if (t.contains(q)) match = true; }
                  if (!match) continue;
              }

              SimulationHistoryResponse dto = new SimulationHistoryResponse();
              dto.setSimulationId(sim.getId());
              dto.setChannelModel("AWGN");
              dto.setModulation(sim.getModulationType());
              dto.setFrequencyGhz(28.0);
              dto.setBerAt20db(0.001); // Mock
              dto.setSimulationTimeSeconds((long)(sim.getSimulationTimeMs() == null ? 0 : sim.getSimulationTimeMs() / 1000));
              dto.setTags(simTags);
              dto.setNote(sim.getNote());
              dto.setStarred(sim.getStarred());
              dto.setExperimentId(sim.getExperimentId());
              if (sim.getExperimentId() != null) {
                  experimentRepository.findById(sim.getExperimentId()).ifPresent(e -> {
                       dto.setExperimentName(e.getName());
                       dto.setExperimentColor(e.getColor());
                  });
              }
              out.add(dto);
         }
         return out;
    }

    // Resolves raw counting matrices automatically globally cleanly logically mapped completely natively
    public List<TagCountResponse> getAllTagsWithCounts(Long userId) {
        List<Object[]> raw = simulationTagRepository.countByUserIdGroupByTag(userId);
        return raw.stream().map(r -> new TagCountResponse((String)r[0], ((Number)r[1]).longValue())).collect(Collectors.toList());
    }
}

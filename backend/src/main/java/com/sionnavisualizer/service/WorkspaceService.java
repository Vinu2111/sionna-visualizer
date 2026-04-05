package com.sionnavisualizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sionnavisualizer.dto.*;
import com.sionnavisualizer.model.*;
import com.sionnavisualizer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Autowired
    private SimulationCommentRepository simulationCommentRepository;

    @Autowired
    private SimulationAnnotationRepository simulationAnnotationRepository;

    @Autowired
    private SimulationVersionRepository simulationVersionRepository;

    @Autowired
    private SimulationResultRepository simulationResultRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ----- WORKSPACE MANAGEMENT -----

    /**
     * Workspace concept: A workspace is a shared collaborative environment (like a Slack/Discord workspace or a GitHub Org).
     * It binds multiple researchers (WorkspaceMembers) to a single logical container.
     * When a user creates a workspace, they automatically become the OWNER, meaning they have full control over settings and billing/permissions.
     */
    @Transactional
    public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request, Long userId) {
        Workspace w = new Workspace();
        w.setName(request.getName());
        w.setDescription(request.getDescription());
        w.setInstitution(request.getInstitution());
        w.setOwnerId(userId);
        Workspace saved = workspaceRepository.save(w);

        WorkspaceMember member = new WorkspaceMember();
        member.setWorkspaceId(saved.getId());
        member.setUserId(userId);
        member.setRole("OWNER");
        workspaceMemberRepository.save(member);

        return mapWorkspace(saved, "OWNER");
    }

    public List<WorkspaceResponse> getUserWorkspaces(Long userId) {
        List<Workspace> list = workspaceRepository.findWorkspacesByUserId(userId);
        return list.stream().map(w -> {
            String role = checkMemberRole(w.getId(), userId);
            return mapWorkspace(w, role);
        }).collect(Collectors.toList());
    }

    private WorkspaceResponse mapWorkspace(Workspace w, String role) {
        WorkspaceResponse res = new WorkspaceResponse();
        res.setWorkspaceId(w.getId());
        res.setName(w.getName());
        res.setDescription(w.getDescription());
        res.setInstitution(w.getInstitution());
        res.setMyRole(role);
        res.setMemberCount((long) workspaceMemberRepository.findByWorkspaceId(w.getId()).size());
        return res;
    }

    // ----- MEMBERSHIP -----

    public void inviteMember(Long workspaceId, String email, String role, Long inviterId) {
        String inviterRole = checkMemberRole(workspaceId, inviterId);
        if(!inviterRole.equals("OWNER") && !inviterRole.equals("ADMIN")) {
            throw new SecurityException("Insufficient permissions logically mapped natively.");
        }
        
        Long mockNewUserId = 99L; // In a full system, you would resolve User via UserRepository natively smoothly
        
        WorkspaceMember m = new WorkspaceMember();
        m.setWorkspaceId(workspaceId);
        m.setUserId(mockNewUserId);
        m.setRole(role);
        workspaceMemberRepository.save(m);
    }
    
    public List<WorkspaceMemberResponse> getWorkspaceMembers(Long workspaceId) {
        return workspaceMemberRepository.findByWorkspaceId(workspaceId).stream().map(m -> {
            WorkspaceMemberResponse r = new WorkspaceMemberResponse();
            r.setUserId(m.getUserId());
            r.setName("Researcher_" + m.getUserId());
            r.setInitials("R" + m.getUserId());
            r.setRole(m.getRole());
            r.setSimulationCount(12L);
            return r;
        }).collect(Collectors.toList());
    }

    // ----- FEED -----

    /**
     * The Feed aggregates activity. In a collaboration app, users don't want to dig through folders.
     * They want a chronological feed of what their lab is doing right now (similar to a GitHub activity feed).
     */
    public List<WorkspaceFeedResponse> getWorkspaceFeed(Long workspaceId, Long requesterId) {
        // Validation: Ensure the requester is actually part of this workspace before they can read the feed.
        checkMemberRole(workspaceId, requesterId); 

        // Fetch all simulations that belong to members of this workspace. 
        // Note: For simplicity in this mock, we just grab everything. 
        // In reality, this would be a JOIN: SELECT s FROM SimulationResult s WHERE s.userId IN (SELECT userId from WorkspaceMember where workspaceId = X)
        List<SimulationResult> all = simulationResultRepository.findAllByOrderByCreatedAtDesc();
        
        return all.stream().limit(10).map(s -> {
             WorkspaceFeedResponse res = new WorkspaceFeedResponse();
             res.setSimulationId(s.getId());
             res.setAuthorName("Investigator");
             res.setAuthorInitials("IN");
             res.setChannelModel("AWGN");
             res.setModulation(s.getModulationType());
             res.setFrequencyGhz(28.0);
             res.setBerAt20db(0.0012);
             res.setAntennas(1);
             res.setCommentCount((long) simulationCommentRepository.findBySimulationIdOrderByIdAsc(s.getId()).size());
             res.setAnnotationCount((long) simulationAnnotationRepository.findBySimulationId(s.getId()).size());
             return res;
        }).collect(Collectors.toList());
    }

    // ----- COMMENTS -----

    public CommentResponse addComment(Long simulationId, String content, Long parentCommentId, Long userId) {
        SimulationComment c = new SimulationComment();
        c.setSimulationId(simulationId);
        c.setUserId(userId);
        c.setAuthorName("User_" + userId);
        c.setContent(content);
        c.setParentCommentId(parentCommentId);
        SimulationComment saved = simulationCommentRepository.save(c);
        
        CommentResponse r = new CommentResponse();
        r.setCommentId(saved.getId());
        r.setAuthorName(saved.getAuthorName());
        r.setContent(saved.getContent());
        r.setParentCommentId(parentCommentId);
        return r;
    }

    public List<CommentResponse> getComments(Long simulationId) {
        List<SimulationComment> parentComments = simulationCommentRepository.findBySimulationIdAndParentCommentIdIsNullOrderByIdAsc(simulationId);
        List<SimulationComment> allComments = simulationCommentRepository.findBySimulationIdOrderByIdAsc(simulationId);

        return parentComments.stream().map(p -> {
            CommentResponse cr = new CommentResponse();
            cr.setCommentId(p.getId());
            cr.setAuthorName(p.getAuthorName());
            cr.setContent(p.getContent());
            cr.setAuthorInitials(p.getAuthorName().substring(0, 2).toUpperCase());
            cr.setReplies(allComments.stream()
                .filter(c -> p.getId().equals(c.getParentCommentId()))
                .map(c -> {
                    CommentResponse r = new CommentResponse();
                    r.setCommentId(c.getId());
                    r.setAuthorName(c.getAuthorName());
                    r.setAuthorInitials(c.getAuthorName().substring(0, 2).toUpperCase());
                    r.setContent(c.getContent());
                    return r;
                }).collect(Collectors.toList()));
            return cr;
        }).collect(Collectors.toList());
    }

    // ----- ANNOTATIONS -----

    public AnnotationResponse addAnnotation(Long simulationId, Double snrPoint, Double berPoint, String text, Long userId) {
        Integer maxPin = simulationAnnotationRepository.findMaxPinNumber(simulationId);
        Integer newPin = maxPin + 1;

        SimulationAnnotation a = new SimulationAnnotation();
        a.setSimulationId(simulationId);
        a.setUserId(userId);
        a.setAuthorName("User_" + userId);
        a.setSnrPoint(snrPoint);
        a.setBerPoint(berPoint);
        a.setAnnotationText(text);
        a.setPinNumber(newPin);
        
        SimulationAnnotation saved = simulationAnnotationRepository.save(a);
        
        AnnotationResponse res = new AnnotationResponse();
        res.setAnnotationId(saved.getId());
        res.setAuthorName(saved.getAuthorName());
        res.setSnrPoint(saved.getSnrPoint());
        res.setBerPoint(saved.getBerPoint());
        res.setText(saved.getAnnotationText());
        res.setPinNumber(saved.getPinNumber());
        return res;
    }

    public List<AnnotationResponse> getAnnotations(Long simulationId) {
        return simulationAnnotationRepository.findBySimulationId(simulationId).stream().map(a -> {
            AnnotationResponse r = new AnnotationResponse();
            r.setAnnotationId(a.getId());
            r.setAuthorName(a.getAuthorName());
            r.setSnrPoint(a.getSnrPoint());
            r.setBerPoint(a.getBerPoint());
            r.setText(a.getAnnotationText());
            r.setPinNumber(a.getPinNumber());
            return r;
        }).collect(Collectors.toList());
    }

    // ----- VERSIONS -----

    /**
     * Version concept: Simulations involve heavily tweaked math parameters (SNR, bandwidth, scattering matrices).
     * Researchers often want to "rollback" a simulation to how it was configured 2 hours ago.
     * 
     * Why snapshot full JSON instead of diffs?
     * Storing diffs (like Git) requires complex conflict resolution and sequential patching to rebuild state.
     * For simulation parameters, state is relatively small. Snapshotting the full JSON string means
     * restoring a version is an O(1) operation: just parse the JSON and overwrite the current record.
     */
    public void saveVersion(Long simulationId, Long userId) throws Exception {
        SimulationResult sim = simulationResultRepository.findById(simulationId).orElseThrow(() -> new IllegalArgumentException("No Sim securely"));
        
        Integer maxV = simulationVersionRepository.findMaxVersionNumber(simulationId);
        Integer newV = maxV + 1;

        SimulationVersion v = new SimulationVersion();
        v.setSimulationId(simulationId);
        v.setUserId(userId);
        v.setCreatedByName("User_" + userId);
        v.setVersionNumber(newV);
        v.setParametersSnapshot(objectMapper.writeValueAsString(sim)); // Snapshots absolute entirety cleanly natively
        v.setChangedFields("[\"snrMin\", \"snrMax\"]"); // Mocks intelligent diff structurally gracefully
        simulationVersionRepository.save(v);
    }

    public List<VersionResponse> getVersionHistory(Long simulationId) {
        return simulationVersionRepository.findBySimulationIdOrderByVersionNumberDesc(simulationId).stream().map(v -> {
             VersionResponse r = new VersionResponse();
             r.setVersionId(v.getId());
             r.setVersionNumber(v.getVersionNumber());
             r.setCreatedByName(v.getCreatedByName());
             r.setChangedFields(List.of("Parameters specifically isolated statically logged neatly")); // Mock logic cleanly mapping natively successfully
             return r;
        }).collect(Collectors.toList());
    }
    
    /**
     * Role Concept & Enforcement check:
     * In collaboration software, simply knowing an ID exists in the database isn't enough.
     * We must check the user's explicit authorization level for the target context (workspace) 
     * before every read or write operation to prevent IDOR (Insecure Direct Object Reference) vulnerabilities.
     */
    public String checkMemberRole(Long workspaceId, Long userId) {
        Optional<WorkspaceMember> m = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId);
        if (m.isEmpty()) throw new SecurityException("User is not a member of this workspace.");
        return m.get().getRole();
    }
}

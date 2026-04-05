package com.sionnavisualizer.controller;

import jakarta.validation.Valid;

import com.sionnavisualizer.dto.*;
import com.sionnavisualizer.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;
    private final Long MOCK_USER_ID = 1L;

    // ----- WORKSPACES -----

    @GetMapping("/workspaces")
    public ResponseEntity<List<WorkspaceResponse>> getWorkspaces() {
        return ResponseEntity.ok(workspaceService.getUserWorkspaces(MOCK_USER_ID));
    }

    @PostMapping("/workspaces")
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) {
        return ResponseEntity.ok(workspaceService.createWorkspace(request, MOCK_USER_ID));
    }

    // ----- MEMBERS -----

    @GetMapping("/workspaces/{id}/members")
    public ResponseEntity<List<WorkspaceMemberResponse>> getMembers(@PathVariable Long id) {
        return ResponseEntity.ok(workspaceService.getWorkspaceMembers(id));
    }

    @PostMapping("/workspaces/{id}/invite")
    public ResponseEntity<Void> inviteMember(@PathVariable Long id, @Valid @RequestBody InviteMemberRequest req) {
        workspaceService.inviteMember(id, req.getEmail(), req.getRole(), MOCK_USER_ID);
        return ResponseEntity.ok().build();
    }

    // ----- FEED -----

    @GetMapping("/workspaces/{id}/feed")
    public ResponseEntity<List<WorkspaceFeedResponse>> getFeed(@PathVariable Long id) {
        return ResponseEntity.ok(workspaceService.getWorkspaceFeed(id, MOCK_USER_ID));
    }

    // ----- COMMENTS -----

    @GetMapping("/simulations/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(workspaceService.getComments(id));
    }

    @PostMapping("/simulations/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long id, @Valid @RequestBody CommentRequest req) {
        return ResponseEntity.ok(workspaceService.addComment(id, req.getContent(), req.getParentCommentId(), MOCK_USER_ID));
    }

    // ----- ANNOTATIONS -----

    @GetMapping("/simulations/{id}/annotations")
    public ResponseEntity<List<AnnotationResponse>> getAnnotations(@PathVariable Long id) {
        return ResponseEntity.ok(workspaceService.getAnnotations(id));
    }

    @PostMapping("/simulations/{id}/annotations")
    public ResponseEntity<AnnotationResponse> addAnnotation(@PathVariable Long id, @Valid @RequestBody AnnotationRequest req) {
        return ResponseEntity.ok(workspaceService.addAnnotation(id, req.getSnrPoint(), req.getBerPoint(), req.getText(), MOCK_USER_ID));
    }

    // ----- VERSIONS -----

    @GetMapping("/simulations/{id}/versions")
    public ResponseEntity<List<VersionResponse>> getVersions(@PathVariable Long id) {
        return ResponseEntity.ok(workspaceService.getVersionHistory(id));
    }

    @PostMapping("/simulations/{id}/versions")
    public ResponseEntity<Void> saveVersion(@PathVariable Long id) {
        try {
            workspaceService.saveVersion(id, MOCK_USER_ID);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

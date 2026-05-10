package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.ArrayList;

public class CommentResponse {
    @NotNull
    @Min(0)
    private Long commentId;
    @NotBlank
    private String authorName;
    @NotBlank
    private String authorInitials;
    @NotBlank
    private String content;
    @NotNull
    @Min(0)
    private Long parentCommentId;
    @NotBlank
    private String createdAt;
    private List<CommentResponse> replies = new ArrayList<>();

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorInitials() { return authorInitials; }
    public void setAuthorInitials(String authorInitials) { this.authorInitials = authorInitials; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Long parentCommentId) { this.parentCommentId = parentCommentId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<CommentResponse> getReplies() { return replies; }
    public void setReplies(List<CommentResponse> replies) { this.replies = replies; }
}

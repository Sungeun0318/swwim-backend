package com.zalmuk.swwim.api.dto.community;

import com.zalmuk.swwim.api.dto.user.UserResponse;
import com.zalmuk.swwim.api.entity.community.PostComment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 댓글 응답 DTO
 */
@Schema(description = "댓글 응답")
public class CommentResponse {

    @Schema(description = "댓글 ID")
    private UUID id;

    @Schema(description = "게시글 ID")
    private UUID postId;

    @Schema(description = "작성자 정보")
    private UserResponse user;

    @Schema(description = "댓글 내용")
    private String text;

    @Schema(description = "좋아요 수")
    private Integer likeCount;

    @Schema(description = "작성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "좋아요 여부 (로그인 사용자)")
    private Boolean isLiked;

    // Constructors
    public CommentResponse() {
    }

    public static CommentResponse from(PostComment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPost().getId());
        response.setUser(UserResponse.summary(comment.getUser()));
        response.setText(comment.getText());
        response.setLikeCount(comment.getLikeCount());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }
}

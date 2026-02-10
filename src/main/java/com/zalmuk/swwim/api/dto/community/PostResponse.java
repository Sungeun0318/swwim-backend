package com.zalmuk.swwim.api.dto.community;

import com.zalmuk.swwim.api.dto.user.UserResponse;
import com.zalmuk.swwim.api.entity.community.CommunityPost;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 게시글 응답 DTO
 */
@Schema(description = "게시글 응답")
public class PostResponse {

    @Schema(description = "게시글 ID")
    private UUID id;

    @Schema(description = "작성자 정보")
    private UserResponse user;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String content;

    @Schema(description = "이미지 URL")
    private String imageUrl;

    @Schema(description = "좋아요 수")
    private Integer likeCount;

    @Schema(description = "댓글 수")
    private Integer commentCount;

    @Schema(description = "공유 수")
    private Integer shareCount;

    @Schema(description = "조회 수")
    private Integer viewCount;

    @Schema(description = "공유 URL")
    private String shareUrl;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    @Schema(description = "좋아요 여부 (로그인 사용자)")
    private Boolean isLiked;

    // Constructors
    public PostResponse() {
    }

    public static PostResponse from(CommunityPost post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setUser(UserResponse.summary(post.getUser()));
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setImageUrl(post.getImageUrl());
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setShareCount(post.getShareCount());
        response.setViewCount(post.getViewCount());
        response.setShareUrl(post.getShareUrl());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        return response;
    }

    public static PostResponse summary(CommunityPost post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setCreatedAt(post.getCreatedAt());
        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }
}

package com.zalmuk.swwim.api.dto.pool;

import com.zalmuk.swwim.api.dto.user.UserResponse;
import com.zalmuk.swwim.api.entity.pool.PoolReview;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 수영장 리뷰 응답 DTO
 */
@Schema(description = "수영장 리뷰 응답")
public class PoolReviewResponse {

    @Schema(description = "리뷰 ID")
    private UUID id;

    @Schema(description = "작성자 정보")
    private UserResponse user;

    @Schema(description = "평점")
    private BigDecimal rating;

    @Schema(description = "리뷰 내용")
    private String comment;

    @Schema(description = "이미지 URL 목록")
    private List<String> images;

    @Schema(description = "작성 시간")
    private LocalDateTime createdAt;

    // Constructors
    public PoolReviewResponse() {
    }

    public static PoolReviewResponse from(PoolReview review) {
        PoolReviewResponse response = new PoolReviewResponse();
        response.setId(review.getId());
        response.setUser(UserResponse.summary(review.getUser()));
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setImages(review.getImages());
        response.setCreatedAt(review.getCreatedAt());
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

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

package com.zalmuk.swwim.api.dto.pool;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 수영장 리뷰 요청 DTO
 */
@Schema(description = "수영장 리뷰 요청")
public class PoolReviewRequest {

    @NotNull(message = "평점은 필수입니다")
    @DecimalMin(value = "0.0", message = "평점은 0 이상이어야 합니다")
    @DecimalMax(value = "5.0", message = "평점은 5 이하여야 합니다")
    @Schema(description = "평점 (0.0 ~ 5.0)", required = true, minimum = "0", maximum = "5")
    private BigDecimal rating;

    @NotBlank(message = "리뷰 내용은 필수입니다")
    @Size(max = 2000, message = "리뷰는 2000자를 초과할 수 없습니다")
    @Schema(description = "리뷰 내용", required = true)
    private String comment;

    @Size(max = 5, message = "이미지는 최대 5개까지 첨부할 수 있습니다")
    @Schema(description = "이미지 URL 목록")
    private List<String> images;

    // Constructors
    public PoolReviewRequest() {
    }

    // Getters and Setters
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
}

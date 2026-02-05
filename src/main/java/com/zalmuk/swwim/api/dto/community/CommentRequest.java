package com.zalmuk.swwim.api.dto.community;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 댓글 요청 DTO
 */
@Schema(description = "댓글 요청")
public class CommentRequest {

    @NotBlank(message = "댓글 내용은 필수입니다")
    @Size(max = 2000, message = "댓글은 2000자를 초과할 수 없습니다")
    @Schema(description = "댓글 내용", required = true)
    private String text;

    // Constructors
    public CommentRequest() {
    }

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

package com.zalmuk.swwim.api.dto.community;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 게시글 요청 DTO
 */
@Schema(description = "게시글 요청")
public class PostRequest {

    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다")
    @Schema(description = "게시글 제목")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    @Size(max = 10000, message = "내용은 10000자를 초과할 수 없습니다")
    @Schema(description = "게시글 내용", required = true)
    private String content;

    @Schema(description = "이미지 URL")
    private String imageUrl;

    // Constructors
    public PostRequest() {
    }

    // Getters and Setters
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
}

package com.zalmuk.swwim.api.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 채팅 메시지 요청 DTO
 */
@Schema(description = "채팅 메시지 요청")
public class ChatMessageRequest {

    @NotBlank(message = "메시지는 필수입니다")
    @Size(max = 5000, message = "메시지는 5000자를 초과할 수 없습니다")
    @Schema(description = "메시지 내용", required = true)
    private String message;

    // Constructors
    public ChatMessageRequest() {
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

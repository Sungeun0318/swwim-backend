package com.zalmuk.swwim.api.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 채팅방 생성 요청 DTO
 */
@Schema(description = "채팅방 생성 요청")
public class CreateChatRoomRequest {

    @NotBlank(message = "상대방 사용자 ID는 필수입니다")
    @Schema(description = "상대방 사용자 ID", required = true)
    private String participantId;

    // Constructors
    public CreateChatRoomRequest() {
    }

    // Getters and Setters
    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }
}

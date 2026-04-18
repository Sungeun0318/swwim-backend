package com.zalmuk.swwim.api.dto.chat;

import com.zalmuk.swwim.api.dto.user.UserResponse;
import com.zalmuk.swwim.api.entity.chat.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 채팅 메시지 응답 DTO
 */
@Schema(description = "채팅 메시지 응답")
public class ChatMessageResponse {

    @Schema(description = "메시지 ID")
    private UUID id;

    @Schema(description = "채팅방 ID")
    private UUID roomId;

    @Schema(description = "발신자 정보")
    private UserResponse sender;

    @Schema(description = "메시지 내용")
    private String message;

    @Schema(description = "읽음 여부")
    private Boolean isRead;

    @Schema(description = "읽은 시간")
    private LocalDateTime readAt;

    @Schema(description = "전송 시간")
    private LocalDateTime createdAt;

    // Constructors
    public ChatMessageResponse() {
    }

    public static ChatMessageResponse from(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setRoomId(message.getRoom().getId());
        response.setSender(UserResponse.summary(message.getSender()));
        response.setMessage(message.getMessage());
        response.setIsRead(message.getIsRead());
        response.setReadAt(message.getReadAt());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public UserResponse getSender() {
        return sender;
    }

    public void setSender(UserResponse sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

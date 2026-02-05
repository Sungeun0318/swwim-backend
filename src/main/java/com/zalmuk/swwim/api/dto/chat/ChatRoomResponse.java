package com.zalmuk.swwim.api.dto.chat;

import com.zalmuk.swwim.api.dto.user.UserResponse;
import com.zalmuk.swwim.api.entity.chat.ChatRoom;
import com.zalmuk.swwim.api.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 채팅방 응답 DTO
 */
@Schema(description = "채팅방 응답")
public class ChatRoomResponse {

    @Schema(description = "채팅방 ID")
    private UUID id;

    @Schema(description = "상대방 정보")
    private UserResponse otherParticipant;

    @Schema(description = "마지막 메시지")
    private String lastMessage;

    @Schema(description = "마지막 메시지 시간")
    private LocalDateTime lastMessageAt;

    @Schema(description = "읽지 않은 메시지 수")
    private Integer unreadCount;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    // Constructors
    public ChatRoomResponse() {
    }

    public static ChatRoomResponse from(ChatRoom room, User currentUser) {
        ChatRoomResponse response = new ChatRoomResponse();
        response.setId(room.getId());
        response.setOtherParticipant(UserResponse.summary(room.getOtherParticipant(currentUser)));
        response.setLastMessage(room.getLastMessage());
        response.setLastMessageAt(room.getLastMessageAt());
        response.setCreatedAt(room.getCreatedAt());
        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserResponse getOtherParticipant() {
        return otherParticipant;
    }

    public void setOtherParticipant(UserResponse otherParticipant) {
        this.otherParticipant = otherParticipant;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

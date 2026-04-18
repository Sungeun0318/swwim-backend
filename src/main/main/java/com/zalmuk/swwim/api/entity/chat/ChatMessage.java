package com.zalmuk.swwim.api.entity.chat;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 채팅 메시지
 */
@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chat_messages_room", columnList = "room_id, created_at DESC"),
        @Index(name = "idx_chat_messages_sender", columnList = "sender_id")
})
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    // 읽음 여부
    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    // Constructors
    protected ChatMessage() {
    }

    public ChatMessage(ChatRoom room, User sender, String message) {
        this.room = room;
        this.sender = sender;
        this.message = message;
    }

    // Helper methods
    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public ChatRoom getRoom() {
        return room;
    }

    public void setRoom(ChatRoom room) {
        this.room = room;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
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
}

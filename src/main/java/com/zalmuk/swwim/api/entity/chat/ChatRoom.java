package com.zalmuk.swwim.api.entity.chat;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 채팅방
 */
@Entity
@Table(name = "chat_rooms",
        uniqueConstraints = @UniqueConstraint(columnNames = {"participant1_id", "participant2_id"}),
        indexes = {
                @Index(name = "idx_chat_rooms_participant1", columnList = "participant1_id"),
                @Index(name = "idx_chat_rooms_participant2", columnList = "participant2_id"),
                @Index(name = "idx_chat_rooms_last_message", columnList = "last_message_at DESC")
        })
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // 참여자 (2명)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant1_id", nullable = false)
    private User participant1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant2_id", nullable = false)
    private User participant2;

    // 마지막 메시지
    @Column(name = "last_message", columnDefinition = "TEXT")
    private String lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    // 연관관계
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<ChatMessage> messages = new ArrayList<>();

    // Constructors
    protected ChatRoom() {
    }

    public ChatRoom(User participant1, User participant2) {
        this.participant1 = participant1;
        this.participant2 = participant2;
    }

    // Helper methods
    public void addMessage(ChatMessage message) {
        messages.add(message);
        message.setRoom(this);
        this.lastMessage = message.getMessage();
        this.lastMessageAt = message.getCreatedAt();
    }

    public boolean isParticipant(User user) {
        return participant1.getId().equals(user.getId()) ||
                participant2.getId().equals(user.getId());
    }

    public User getOtherParticipant(User user) {
        if (participant1.getId().equals(user.getId())) {
            return participant2;
        }
        return participant1;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public User getParticipant1() {
        return participant1;
    }

    public void setParticipant1(User participant1) {
        this.participant1 = participant1;
    }

    public User getParticipant2() {
        return participant2;
    }

    public void setParticipant2(User participant2) {
        this.participant2 = participant2;
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

    public List<ChatMessage> getMessages() {
        return messages;
    }
}

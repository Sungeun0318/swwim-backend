package com.zalmuk.swwim.api.entity.notification;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import com.zalmuk.swwim.api.entity.enums.NotificationType;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 알림
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notifications_user", columnList = "user_id, created_at DESC")
})
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    // 관련 데이터
    @Column(name = "related_id")
    private String relatedId; // 관련 게시글/사용자 등의 ID

    @Column(name = "action_url", columnDefinition = "TEXT")
    private String actionUrl;

    // 읽음 여부
    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    // 예약 발송 (훈련 리마인더용)
    @Column(name = "scheduled_for")
    private LocalDateTime scheduledFor;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    // Constructors
    protected Notification() {
    }

    public Notification(User user, NotificationType type, String title, String body) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.body = body;
    }

    // Helper methods
    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = LocalDateTime.now();
        }
    }

    public void markAsSent() {
        this.sentAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
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

    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(LocalDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}

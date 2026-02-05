package com.zalmuk.swwim.api.dto.notification;

import com.zalmuk.swwim.api.entity.enums.NotificationType;
import com.zalmuk.swwim.api.entity.notification.Notification;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 알림 응답 DTO
 */
@Schema(description = "알림 응답")
public class NotificationResponse {

    @Schema(description = "알림 ID")
    private UUID id;

    @Schema(description = "알림 타입")
    private NotificationType type;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String body;

    @Schema(description = "관련 ID (게시글/사용자 등)")
    private String relatedId;

    @Schema(description = "액션 URL")
    private String actionUrl;

    @Schema(description = "읽음 여부")
    private Boolean isRead;

    @Schema(description = "읽은 시간")
    private LocalDateTime readAt;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    // Constructors
    public NotificationResponse() {
    }

    public static NotificationResponse from(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setType(notification.getType());
        response.setTitle(notification.getTitle());
        response.setBody(notification.getBody());
        response.setRelatedId(notification.getRelatedId());
        response.setActionUrl(notification.getActionUrl());
        response.setIsRead(notification.getIsRead());
        response.setReadAt(notification.getReadAt());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

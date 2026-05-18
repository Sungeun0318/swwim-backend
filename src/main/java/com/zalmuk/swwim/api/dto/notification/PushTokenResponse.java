package com.zalmuk.swwim.api.dto.notification;

import com.zalmuk.swwim.api.entity.notification.UserPushToken;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "푸시 토큰 응답")
public class PushTokenResponse {

    private UUID id;
    private String platform;
    private String provider;
    private String deviceId;
    private String appVersion;
    private Boolean enabled;
    private Integer failureCount;
    private LocalDateTime lastUsedAt;
    private LocalDateTime createdAt;

    public static PushTokenResponse from(UserPushToken token) {
        PushTokenResponse response = new PushTokenResponse();
        response.setId(token.getId());
        response.setPlatform(token.getPlatform());
        response.setProvider(token.getProvider());
        response.setDeviceId(token.getDeviceId());
        response.setAppVersion(token.getAppVersion());
        response.setEnabled(token.getEnabled());
        response.setFailureCount(token.getFailureCount());
        response.setLastUsedAt(token.getLastUsedAt());
        response.setCreatedAt(token.getCreatedAt());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

package com.zalmuk.swwim.api.entity.notification;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자 외부 푸시 토큰.
 */
@Entity
@Table(name = "user_push_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_push_tokens_user_token", columnNames = {"user_id", "token"})
        },
        indexes = {
                @Index(name = "idx_push_tokens_user_enabled", columnList = "user_id, enabled")
        })
public class UserPushToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "platform", nullable = false, length = 10)
    private String platform;

    @Column(name = "provider", nullable = false, length = 10)
    private String provider;

    @Column(name = "token", nullable = false, columnDefinition = "TEXT")
    private String token;

    @Column(name = "device_id", length = 128)
    private String deviceId;

    @Column(name = "app_version", length = 20)
    private String appVersion;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "failure_count", nullable = false)
    private Integer failureCount = 0;

    protected UserPushToken() {
    }

    public UserPushToken(User user, String platform, String provider, String token) {
        this.user = user;
        this.platform = platform;
        this.provider = provider;
        this.token = token;
        touch();
    }

    public void update(String platform, String provider, String deviceId, String appVersion) {
        this.platform = platform;
        this.provider = provider;
        this.deviceId = deviceId;
        this.appVersion = appVersion;
        this.enabled = true;
        touch();
    }

    public void disable() {
        this.enabled = false;
    }

    public void touch() {
        this.lastUsedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }
}

package com.zalmuk.swwim.api.entity.user;

import com.zalmuk.swwim.api.entity.enums.EntitlementSource;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자 기능 권한 (Feature Layer)
 * - premium: 프리미엄 전체 기능
 * - custom_training: 커스텀 훈련 설정
 * - custom_beep_sound: 음성 출발 신호 변경
 * - multi_player_mode: 타종 타이머 / 복수선수 모드
 * - ad_free: 광고 제거
 */
@Entity
@Table(name = "user_entitlements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "entitlement"}),
        indexes = {
                @Index(name = "idx_entitlements_user", columnList = "user_id"),
                @Index(name = "idx_entitlements_entitlement", columnList = "entitlement")
        })
public class UserEntitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "entitlement", nullable = false, length = 50)
    private String entitlement;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private EntitlementSource source;

    // 관리자 수동 부여 시 누가 부여했는지
    @Column(name = "granted_by", length = 128)
    private String grantedBy;

    // null이면 영구, 값이 있으면 해당 시점에 만료
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected UserEntitlement() {
    }

    public UserEntitlement(User user, String entitlement, EntitlementSource source) {
        this.user = user;
        this.entitlement = entitlement;
        this.source = source;
        this.createdAt = LocalDateTime.now();
    }

    // 만료 여부 확인
    public boolean isExpired() {
        if (expiresAt == null) return false; // 영구
        return LocalDateTime.now().isAfter(expiresAt);
    }

    // 유효한 entitlement인지 확인
    public boolean isActive() {
        return !isExpired();
    }

    // Getters and Setters
    public UUID getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEntitlement() { return entitlement; }
    public void setEntitlement(String entitlement) { this.entitlement = entitlement; }

    public EntitlementSource getSource() { return source; }
    public void setSource(EntitlementSource source) { this.source = source; }

    public String getGrantedBy() { return grantedBy; }
    public void setGrantedBy(String grantedBy) { this.grantedBy = grantedBy; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

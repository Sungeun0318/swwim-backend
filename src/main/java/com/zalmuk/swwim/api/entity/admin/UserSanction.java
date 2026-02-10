package com.zalmuk.swwim.api.entity.admin;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import com.zalmuk.swwim.api.entity.enums.SanctionType;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자 제재
 */
@Entity
@Table(name = "user_sanctions", indexes = {
        @Index(name = "idx_user_sanctions_user", columnList = "user_id")
})
public class UserSanction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "sanction_type", nullable = false, length = 20)
    private SanctionType sanctionType;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    // 제재 기간
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    // 상태
    @Column(name = "is_active")
    private Boolean isActive = true;

    // Constructors
    protected UserSanction() {
    }

    public UserSanction(User user, User admin, SanctionType sanctionType, String reason) {
        this.user = user;
        this.admin = admin;
        this.sanctionType = sanctionType;
        this.reason = reason;
        this.startDate = LocalDateTime.now();
    }

    // Helper methods
    public void deactivate() {
        this.isActive = false;
    }

    public boolean isExpired() {
        if (endDate == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(endDate);
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

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public SanctionType getSanctionType() {
        return sanctionType;
    }

    public void setSanctionType(SanctionType sanctionType) {
        this.sanctionType = sanctionType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

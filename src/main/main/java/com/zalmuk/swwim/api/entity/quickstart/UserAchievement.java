package com.zalmuk.swwim.api.entity.quickstart;

import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 사용자 업적
 */
@Entity
@Table(name = "user_achievements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "achievement_type", "achievement_name"}))
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "achievement_type", nullable = false, length = 50)
    private String achievementType; // 'quickstart_daily', 'quickstart_weekly', 'distance_milestone', etc.

    @Column(name = "achievement_name", nullable = false, length = 100)
    private String achievementName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "achievement_data", columnDefinition = "jsonb")
    private Map<String, Object> achievementData;

    @Column(name = "earned_at")
    private LocalDateTime earnedAt;

    // Constructors
    protected UserAchievement() {
    }

    public UserAchievement(User user, String achievementType, String achievementName) {
        this.user = user;
        this.achievementType = achievementType;
        this.achievementName = achievementName;
        this.earnedAt = LocalDateTime.now();
    }

    // PrePersist
    @PrePersist
    public void prePersist() {
        if (earnedAt == null) {
            earnedAt = LocalDateTime.now();
        }
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

    public String getAchievementType() {
        return achievementType;
    }

    public void setAchievementType(String achievementType) {
        this.achievementType = achievementType;
    }

    public String getAchievementName() {
        return achievementName;
    }

    public void setAchievementName(String achievementName) {
        this.achievementName = achievementName;
    }

    public Map<String, Object> getAchievementData() {
        return achievementData;
    }

    public void setAchievementData(Map<String, Object> achievementData) {
        this.achievementData = achievementData;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }
}

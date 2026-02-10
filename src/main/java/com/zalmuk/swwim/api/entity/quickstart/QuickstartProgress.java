package com.zalmuk.swwim.api.entity.quickstart;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.enums.UserLevel;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 퀵스타트 진행 상황
 */
@Entity
@Table(name = "quickstart_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "level"}),
        indexes = {
                @Index(name = "idx_quickstart_progress_user", columnList = "user_id, level")
        })
public class QuickstartProgress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 20)
    private UserLevel level;

    @Column(name = "current_week")
    private Integer currentWeek = 1;

    @Column(name = "last_completed_at")
    private LocalDateTime lastCompletedAt;

    // 연관관계
    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("week ASC")
    private List<QuickstartWeekProgress> weekProgresses = new ArrayList<>();

    // Constructors
    protected QuickstartProgress() {
    }

    public QuickstartProgress(User user, UserLevel level) {
        this.user = user;
        this.level = level;
    }

    // Helper methods
    public void addWeekProgress(QuickstartWeekProgress weekProgress) {
        weekProgresses.add(weekProgress);
        weekProgress.setProgress(this);
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

    public UserLevel getLevel() {
        return level;
    }

    public void setLevel(UserLevel level) {
        this.level = level;
    }

    public Integer getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(Integer currentWeek) {
        this.currentWeek = currentWeek;
    }

    public LocalDateTime getLastCompletedAt() {
        return lastCompletedAt;
    }

    public void setLastCompletedAt(LocalDateTime lastCompletedAt) {
        this.lastCompletedAt = lastCompletedAt;
    }

    public List<QuickstartWeekProgress> getWeekProgresses() {
        return weekProgresses;
    }
}

package com.zalmuk.swwim.api.entity.user;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 통계
 */
@Entity
@Table(name = "user_stats", indexes = {
        @Index(name = "idx_user_stats_last_training", columnList = "last_training_date")
})
public class UserStats {

    @Id
    @Column(name = "user_id", length = 128)
    private String userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    // 훈련 통계
    @Column(name = "total_sessions")
    private Integer totalSessions = 0;

    @Column(name = "total_distance")
    private Integer totalDistance = 0; // 미터

    @Column(name = "total_time")
    private Integer totalTime = 0; // 초

    // 월간 통계
    @Column(name = "monthly_sessions")
    private Integer monthlySessions = 0;

    @Column(name = "monthly_distance")
    private Integer monthlyDistance = 0;

    @Column(name = "monthly_time")
    private Integer monthlyTime = 0;

    // 연속 기록
    @Column(name = "current_streak")
    private Integer currentStreak = 0;

    @Column(name = "longest_streak")
    private Integer longestStreak = 0;

    // 개인 기록
    @Column(name = "best_100m_time")
    private Integer best100mTime; // 초

    @Column(name = "best_200m_time")
    private Integer best200mTime;

    @Column(name = "best_500m_time")
    private Integer best500mTime;

    // 마지막 훈련
    @Column(name = "last_training_date")
    private LocalDate lastTrainingDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    protected UserStats() {
    }

    public UserStats(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    // PreUpdate
    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Integer getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Integer totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getMonthlySessions() {
        return monthlySessions;
    }

    public void setMonthlySessions(Integer monthlySessions) {
        this.monthlySessions = monthlySessions;
    }

    public Integer getMonthlyDistance() {
        return monthlyDistance;
    }

    public void setMonthlyDistance(Integer monthlyDistance) {
        this.monthlyDistance = monthlyDistance;
    }

    public Integer getMonthlyTime() {
        return monthlyTime;
    }

    public void setMonthlyTime(Integer monthlyTime) {
        this.monthlyTime = monthlyTime;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(Integer longestStreak) {
        this.longestStreak = longestStreak;
    }

    public Integer getBest100mTime() {
        return best100mTime;
    }

    public void setBest100mTime(Integer best100mTime) {
        this.best100mTime = best100mTime;
    }

    public Integer getBest200mTime() {
        return best200mTime;
    }

    public void setBest200mTime(Integer best200mTime) {
        this.best200mTime = best200mTime;
    }

    public Integer getBest500mTime() {
        return best500mTime;
    }

    public void setBest500mTime(Integer best500mTime) {
        this.best500mTime = best500mTime;
    }

    public LocalDate getLastTrainingDate() {
        return lastTrainingDate;
    }

    public void setLastTrainingDate(LocalDate lastTrainingDate) {
        this.lastTrainingDate = lastTrainingDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

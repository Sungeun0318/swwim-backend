package com.zalmuk.swwim.api.entity.quickstart;

import com.zalmuk.swwim.api.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 주차별 진행 상황
 */
@Entity
@Table(name = "quickstart_week_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"progress_id", "week"}))
public class QuickstartWeekProgress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "progress_id", nullable = false)
    private QuickstartProgress progress;

    @Column(name = "week", nullable = false)
    private Integer week;

    @Column(name = "is_unlocked")
    private Boolean isUnlocked = false;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "completed_days")
    private Integer completedDays = 0;

    @Column(name = "total_time")
    private Integer totalTime = 0;

    @Column(name = "total_distance")
    private Integer totalDistance = 0;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // 연관관계
    @OneToMany(mappedBy = "weekProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("completionDate ASC")
    private List<QuickstartDailyCompletion> dailyCompletions = new ArrayList<>();

    // Constructors
    protected QuickstartWeekProgress() {
    }

    public QuickstartWeekProgress(QuickstartProgress progress, Integer week) {
        this.progress = progress;
        this.week = week;
    }

    // Helper methods
    public void addDailyCompletion(QuickstartDailyCompletion completion) {
        dailyCompletions.add(completion);
        completion.setWeekProgress(this);
        this.completedDays++;
        this.totalTime += completion.getTotalTime();
        this.totalDistance += completion.getTotalDistance();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public QuickstartProgress getProgress() {
        return progress;
    }

    public void setProgress(QuickstartProgress progress) {
        this.progress = progress;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Boolean getIsUnlocked() {
        return isUnlocked;
    }

    public void setIsUnlocked(Boolean isUnlocked) {
        this.isUnlocked = isUnlocked;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Integer getCompletedDays() {
        return completedDays;
    }

    public void setCompletedDays(Integer completedDays) {
        this.completedDays = completedDays;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Integer totalDistance) {
        this.totalDistance = totalDistance;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<QuickstartDailyCompletion> getDailyCompletions() {
        return dailyCompletions;
    }
}

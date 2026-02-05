package com.zalmuk.swwim.api.entity.quickstart;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 일일 완료 기록
 */
@Entity
@Table(name = "quickstart_daily_completions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"week_progress_id", "completion_date"}))
public class QuickstartDailyCompletion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_progress_id", nullable = false)
    private QuickstartWeekProgress weekProgress;

    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;

    @Column(name = "total_time", nullable = false)
    private Integer totalTime;

    @Column(name = "total_distance", nullable = false)
    private Integer totalDistance;

    // Constructors
    protected QuickstartDailyCompletion() {
    }

    public QuickstartDailyCompletion(QuickstartWeekProgress weekProgress, LocalDate completionDate, Integer totalTime, Integer totalDistance) {
        this.weekProgress = weekProgress;
        this.completionDate = completionDate;
        this.totalTime = totalTime;
        this.totalDistance = totalDistance;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public QuickstartWeekProgress getWeekProgress() {
        return weekProgress;
    }

    public void setWeekProgress(QuickstartWeekProgress weekProgress) {
        this.weekProgress = weekProgress;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
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
}

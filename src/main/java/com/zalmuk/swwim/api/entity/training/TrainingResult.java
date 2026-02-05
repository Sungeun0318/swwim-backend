package com.zalmuk.swwim.api.entity.training;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 훈련 결과
 */
@Entity
@Table(name = "training_results", indexes = {
        @Index(name = "idx_training_results_user", columnList = "user_id"),
        @Index(name = "idx_training_results_session", columnList = "session_id"),
        @Index(name = "idx_training_results_completed", columnList = "completed_at DESC")
})
public class TrainingResult extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private TrainingSession session;

    @Column(name = "total_time", nullable = false, length = 20)
    private String totalTime; // "HH:MM:SS" 형식

    @Column(name = "total_distance", nullable = false)
    private Integer totalDistance;

    @Column(name = "added_to_calendar")
    private Boolean addedToCalendar = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // 연관관계
    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<TrainingResultDetail> details = new ArrayList<>();

    // Constructors
    protected TrainingResult() {
    }

    public TrainingResult(User user, TrainingSession session, String totalTime, Integer totalDistance) {
        this.user = user;
        this.session = session;
        this.totalTime = totalTime;
        this.totalDistance = totalDistance;
        this.completedAt = LocalDateTime.now();
    }

    // Helper methods
    public void addDetail(TrainingResultDetail detail) {
        details.add(detail);
        detail.setResult(this);
    }

    public void removeDetail(TrainingResultDetail detail) {
        details.remove(detail);
        detail.setResult(null);
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

    public TrainingSession getSession() {
        return session;
    }

    public void setSession(TrainingSession session) {
        this.session = session;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Integer totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Boolean getAddedToCalendar() {
        return addedToCalendar;
    }

    public void setAddedToCalendar(Boolean addedToCalendar) {
        this.addedToCalendar = addedToCalendar;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<TrainingResultDetail> getDetails() {
        return details;
    }
}

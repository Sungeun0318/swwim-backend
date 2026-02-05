package com.zalmuk.swwim.api.entity.training;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.enums.TrainingStatus;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 훈련 세션
 */
@Entity
@Table(name = "training_sessions", indexes = {
        @Index(name = "idx_training_sessions_user", columnList = "user_id"),
        @Index(name = "idx_training_sessions_status", columnList = "status"),
        @Index(name = "idx_training_sessions_created", columnList = "created_at DESC")
})
public class TrainingSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "beep_sound", nullable = false, length = 50)
    private String beepSound;

    @Column(name = "num_people", nullable = false)
    private Integer numPeople = 1;

    // 통계
    @Column(name = "total_time", nullable = false)
    private Integer totalTime; // 초

    @Column(name = "total_distance", nullable = false)
    private Integer totalDistance; // 미터

    // 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private TrainingStatus status = TrainingStatus.CREATED;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    // 메타데이터 (JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // 연관관계
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<TrainingDetail> details = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainingResult> results = new ArrayList<>();

    // Constructors
    protected TrainingSession() {
    }

    public TrainingSession(User user, String beepSound, Integer totalTime, Integer totalDistance) {
        this.user = user;
        this.beepSound = beepSound;
        this.totalTime = totalTime;
        this.totalDistance = totalDistance;
    }

    // Helper methods
    public void addDetail(TrainingDetail detail) {
        details.add(detail);
        detail.setSession(this);
    }

    public void removeDetail(TrainingDetail detail) {
        details.remove(detail);
        detail.setSession(null);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBeepSound() {
        return beepSound;
    }

    public void setBeepSound(String beepSound) {
        this.beepSound = beepSound;
    }

    public Integer getNumPeople() {
        return numPeople;
    }

    public void setNumPeople(Integer numPeople) {
        this.numPeople = numPeople;
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

    public TrainingStatus getStatus() {
        return status;
    }

    public void setStatus(TrainingStatus status) {
        this.status = status;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<TrainingDetail> getDetails() {
        return details;
    }

    public List<TrainingResult> getResults() {
        return results;
    }
}

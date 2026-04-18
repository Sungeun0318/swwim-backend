package com.zalmuk.swwim.api.entity.training;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 캘린더 이벤트
 */
@Entity
@Table(name = "calendar_events",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}),
        indexes = {
                @Index(name = "idx_calendar_events_user_date", columnList = "user_id, date DESC")
        })
public class CalendarEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "total_distance")
    private Integer totalDistance;

    @Column(name = "total_time", length = 20)
    private String totalTime;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "memo", columnDefinition = "text")
    private String memo;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "difficulty", length = 50)
    private String difficulty;

    // 훈련 데이터 (JSON 배열)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trainings", columnDefinition = "jsonb")
    private List<Map<String, Object>> trainings;

    // 자동 저장 여부
    @Column(name = "auto_saved")
    private Boolean autoSaved = false;

    // 알림 설정
    @Column(name = "scheduled_date_time")
    private LocalDateTime scheduledDateTime;

    @Column(name = "notify_30min_before")
    private Boolean notify30minBefore = false;

    @Column(name = "notify_1hour_before")
    private Boolean notify1hourBefore = false;

    // Constructors
    protected CalendarEvent() {
    }

    public CalendarEvent(User user, LocalDate date, List<Map<String, Object>> trainings) {
        this.user = user;
        this.date = date;
        this.trainings = trainings;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Map<String, Object>> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<Map<String, Object>> trainings) {
        this.trainings = trainings;
    }

    public Boolean getAutoSaved() {
        return autoSaved;
    }

    public void setAutoSaved(Boolean autoSaved) {
        this.autoSaved = autoSaved;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public Boolean getNotify30minBefore() {
        return notify30minBefore;
    }

    public void setNotify30minBefore(Boolean notify30minBefore) {
        this.notify30minBefore = notify30minBefore;
    }

    public Boolean getNotify1hourBefore() {
        return notify1hourBefore;
    }

    public void setNotify1hourBefore(Boolean notify1hourBefore) {
        this.notify1hourBefore = notify1hourBefore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Integer totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}

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

    // 훈련 데이터 (JSON 배열)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trainings", nullable = false, columnDefinition = "jsonb")
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
}

package com.zalmuk.swwim.api.dto.calendar;

import com.zalmuk.swwim.api.entity.training.CalendarEvent;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 캘린더 이벤트 응답 DTO
 */
@Schema(description = "캘린더 이벤트 응답")
public class CalendarEventResponse {

    private UUID id;
    private String userId;
    private LocalDate date;
    private String title;
    private Integer totalDistance;
    private String totalTime;
    private String sessionId;
    private Instant startedAt;
    private Instant endedAt;
    private Boolean completed;
    private String memo;
    private String type;
    private String difficulty;
    private List<Map<String, Object>> trainings;
    private Boolean autoSaved;
    private LocalDateTime scheduledDateTime;
    private Boolean notify30minBefore;
    private Boolean notify1hourBefore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CalendarEventResponse() {
    }

    public static CalendarEventResponse from(CalendarEvent event) {
        CalendarEventResponse response = new CalendarEventResponse();
        response.setId(event.getId());
        response.setUserId(event.getUser().getId());
        response.setDate(event.getDate());
        response.setTitle(event.getTitle());
        response.setTotalDistance(event.getTotalDistance());
        response.setTotalTime(event.getTotalTime());
        response.setSessionId(event.getSessionId());
        response.setStartedAt(event.getStartedAt());
        response.setEndedAt(event.getEndedAt());
        response.setCompleted(event.getCompleted());
        response.setMemo(event.getMemo());
        response.setType(event.getType());
        response.setDifficulty(event.getDifficulty());
        response.setTrainings(event.getTrainings());
        response.setAutoSaved(event.getAutoSaved());
        response.setScheduledDateTime(event.getScheduledDateTime());
        response.setNotify30minBefore(event.getNotify30minBefore());
        response.setNotify1hourBefore(event.getNotify1hourBefore());
        response.setCreatedAt(event.getCreatedAt());
        response.setUpdatedAt(event.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Integer totalDistance) { this.totalDistance = totalDistance; }
    public String getTotalTime() { return totalTime; }
    public void setTotalTime(String totalTime) { this.totalTime = totalTime; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getEndedAt() { return endedAt; }
    public void setEndedAt(Instant endedAt) { this.endedAt = endedAt; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public List<Map<String, Object>> getTrainings() { return trainings; }
    public void setTrainings(List<Map<String, Object>> trainings) { this.trainings = trainings; }
    public Boolean getAutoSaved() { return autoSaved; }
    public void setAutoSaved(Boolean autoSaved) { this.autoSaved = autoSaved; }
    public LocalDateTime getScheduledDateTime() { return scheduledDateTime; }
    public void setScheduledDateTime(LocalDateTime scheduledDateTime) { this.scheduledDateTime = scheduledDateTime; }
    public Boolean getNotify30minBefore() { return notify30minBefore; }
    public void setNotify30minBefore(Boolean notify30minBefore) { this.notify30minBefore = notify30minBefore; }
    public Boolean getNotify1hourBefore() { return notify1hourBefore; }
    public void setNotify1hourBefore(Boolean notify1hourBefore) { this.notify1hourBefore = notify1hourBefore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

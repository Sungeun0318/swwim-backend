package com.zalmuk.swwim.api.dto.calendar;

import com.zalmuk.swwim.api.entity.training.CalendarEvent;
import io.swagger.v3.oas.annotations.media.Schema;

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

    @Schema(description = "이벤트 ID")
    private UUID id;

    @Schema(description = "사용자 ID")
    private String userId;

    @Schema(description = "날짜")
    private LocalDate date;

    @Schema(description = "훈련 데이터 목록")
    private List<Map<String, Object>> trainings;

    @Schema(description = "자동 저장 여부")
    private Boolean autoSaved;

    @Schema(description = "예약 시간")
    private LocalDateTime scheduledDateTime;

    @Schema(description = "30분 전 알림")
    private Boolean notify30minBefore;

    @Schema(description = "1시간 전 알림")
    private Boolean notify1hourBefore;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    // Constructors
    public CalendarEventResponse() {
    }

    public static CalendarEventResponse from(CalendarEvent event) {
        CalendarEventResponse response = new CalendarEventResponse();
        response.setId(event.getId());
        response.setUserId(event.getUser().getId());
        response.setDate(event.getDate());
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
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

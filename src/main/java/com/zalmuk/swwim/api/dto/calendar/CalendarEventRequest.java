package com.zalmuk.swwim.api.dto.calendar;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 캘린더 이벤트 요청 DTO
 */
@Schema(description = "캘린더 이벤트 요청")
public class CalendarEventRequest {

    @NotNull(message = "훈련 데이터는 필수입니다")
    @NotEmpty(message = "훈련 데이터는 비어있을 수 없습니다")
    @Schema(description = "훈련 데이터 목록", required = true)
    private List<Map<String, Object>> trainings;

    @Schema(description = "자동 저장 여부")
    private Boolean autoSaved = false;

    @Schema(description = "예약 시간")
    private LocalDateTime scheduledDateTime;

    @Schema(description = "30분 전 알림")
    private Boolean notify30minBefore = false;

    @Schema(description = "1시간 전 알림")
    private Boolean notify1hourBefore = false;

    // Constructors
    public CalendarEventRequest() {
    }

    // Getters and Setters
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

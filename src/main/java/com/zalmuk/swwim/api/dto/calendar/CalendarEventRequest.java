package com.zalmuk.swwim.api.dto.calendar;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 캘린더 이벤트 요청 DTO
 */
@Schema(description = "캘린더 이벤트 요청")
public class CalendarEventRequest {

    @Schema(description = "제목")
    private String title;

    @Schema(description = "총 거리")
    private Integer totalDistance;

    @Schema(description = "총 시간")
    private String totalTime;

    @Schema(description = "훈련 데이터 목록")
    private List<Map<String, Object>> trainings;

    @Schema(description = "자동 저장 여부")
    private Boolean autoSaved = false;

    @Schema(description = "예약 시간")
    private LocalDateTime scheduledDateTime;

    @Schema(description = "30분 전 알림")
    private Boolean notify30minBefore = false;

    @Schema(description = "1시간 전 알림")
    private Boolean notify1hourBefore = false;

    @Schema(description = "메모")
    private String memo;

    @Schema(description = "유형")
    private String type;

    @Schema(description = "난이도")
    private String difficulty;

    public CalendarEventRequest() {
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Integer totalDistance) { this.totalDistance = totalDistance; }
    public String getTotalTime() { return totalTime; }
    public void setTotalTime(String totalTime) { this.totalTime = totalTime; }
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
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}

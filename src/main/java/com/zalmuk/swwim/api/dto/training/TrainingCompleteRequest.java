package com.zalmuk.swwim.api.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * 훈련 완료 요청 DTO
 */
@Schema(description = "훈련 완료 요청")
public class TrainingCompleteRequest {

    @Schema(description = "총 시간 (초)", example = "5400")
    private Integer totalTime;

    @Schema(description = "총 거리 (미터)")
    private Integer totalDistance;

    @Schema(description = "상세 결과 목록")
    private List<TrainingResultDetailRequest> details;

    @Schema(description = "훈련 시작 시각 (UTC epoch millis)")
    private Long startedAtEpochMs;

    @Schema(description = "훈련 종료 시각 (UTC epoch millis)")
    private Long endedAtEpochMs;

    // Constructors
    public TrainingCompleteRequest() {
    }

    // Getters and Setters
    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    /**
     * 총 시간을 HH:mm:ss 문자열로 변환
     */
    public String getTotalTimeAsString() {
        if (totalTime == null) return "00:00:00";
        int hours = totalTime / 3600;
        int minutes = (totalTime % 3600) / 60;
        int seconds = totalTime % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public Integer getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Integer totalDistance) {
        this.totalDistance = totalDistance;
    }

    public List<TrainingResultDetailRequest> getDetails() {
        return details;
    }

    public void setDetails(List<TrainingResultDetailRequest> details) {
        this.details = details;
    }

    public Long getStartedAtEpochMs() {
        return startedAtEpochMs;
    }

    public void setStartedAtEpochMs(Long startedAtEpochMs) {
        this.startedAtEpochMs = startedAtEpochMs;
    }

    public Long getEndedAtEpochMs() {
        return endedAtEpochMs;
    }

    public void setEndedAtEpochMs(Long endedAtEpochMs) {
        this.endedAtEpochMs = endedAtEpochMs;
    }

    public Instant getStartedAt() {
        return startedAtEpochMs != null ? Instant.ofEpochMilli(startedAtEpochMs) : null;
    }

    public Instant getEndedAt() {
        return endedAtEpochMs != null ? Instant.ofEpochMilli(endedAtEpochMs) : null;
    }

    /**
     * 상세 결과 요청 DTO
     */
    @Schema(description = "상세 결과 요청")
    public static class TrainingResultDetailRequest {

        @Schema(description = "훈련 상세 제목")
        private String title;

        @Schema(description = "거리 (미터)")
        private Integer distance;

        @Schema(description = "시간 (초)")
        private Integer time;

        @Schema(description = "랩 번호")
        private Integer lapNumber;

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getDistance() {
            return distance;
        }

        public void setDistance(Integer distance) {
            this.distance = distance;
        }

        public Integer getTime() {
            return time;
        }

        public void setTime(Integer time) {
            this.time = time;
        }

        public Integer getLapNumber() {
            return lapNumber;
        }

        public void setLapNumber(Integer lapNumber) {
            this.lapNumber = lapNumber;
        }
    }
}

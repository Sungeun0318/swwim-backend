package com.zalmuk.swwim.api.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * 훈련 완료 요청 DTO
 */
@Schema(description = "훈련 완료 요청")
public class TrainingCompleteRequest {

    @NotBlank(message = "총 시간은 필수입니다")
    @Schema(description = "총 시간 (HH:mm:ss)", required = true, example = "01:30:00")
    private String totalTime;

    @NotNull(message = "총 거리는 필수입니다")
    @Positive(message = "총 거리는 0보다 커야 합니다")
    @Schema(description = "총 거리 (미터)", required = true)
    private Integer totalDistance;

    @Schema(description = "상세 결과 목록")
    private List<TrainingResultDetailRequest> details;

    // Constructors
    public TrainingCompleteRequest() {
    }

    // Getters and Setters
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

    public List<TrainingResultDetailRequest> getDetails() {
        return details;
    }

    public void setDetails(List<TrainingResultDetailRequest> details) {
        this.details = details;
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

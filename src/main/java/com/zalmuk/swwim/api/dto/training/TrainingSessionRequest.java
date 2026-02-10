package com.zalmuk.swwim.api.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * 훈련 세션 생성 요청 DTO
 */
@Schema(description = "훈련 세션 생성 요청")
public class TrainingSessionRequest {

    @Schema(description = "훈련 제목")
    private String title;

    @Schema(description = "비프음", defaultValue = "beep1")
    private String beepSound = "beep1";

    @Positive(message = "인원 수는 1 이상이어야 합니다")
    @Schema(description = "인원 수", defaultValue = "1")
    private Integer numPeople = 1;

    @NotNull(message = "총 시간은 필수입니다")
    @Positive(message = "총 시간은 0보다 커야 합니다")
    @Schema(description = "총 시간 (초)", required = true)
    private Integer totalTime;

    @NotNull(message = "총 거리는 필수입니다")
    @Positive(message = "총 거리는 0보다 커야 합니다")
    @Schema(description = "총 거리 (미터)", required = true)
    private Integer totalDistance;

    @Valid
    @Schema(description = "훈련 상세 목록")
    private List<TrainingDetailRequest> details;

    // Constructors
    public TrainingSessionRequest() {
    }

    // Getters and Setters
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

    public List<TrainingDetailRequest> getDetails() {
        return details;
    }

    public void setDetails(List<TrainingDetailRequest> details) {
        this.details = details;
    }
}

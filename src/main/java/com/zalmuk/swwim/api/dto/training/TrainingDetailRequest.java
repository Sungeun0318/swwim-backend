package com.zalmuk.swwim.api.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * 훈련 상세 요청 DTO
 */
@Schema(description = "훈련 상세 요청")
public class TrainingDetailRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Schema(description = "제목", required = true)
    private String title;

    @NotNull(message = "거리는 필수입니다")
    @Positive(message = "거리는 0보다 커야 합니다")
    @Schema(description = "거리 (미터)", required = true)
    private Integer distance;

    @NotNull(message = "횟수는 필수입니다")
    @Positive(message = "횟수는 0보다 커야 합니다")
    @Schema(description = "횟수", required = true)
    private Integer count;

    @PositiveOrZero(message = "사이클은 0 이상이어야 합니다")
    @Schema(description = "사이클")
    private Integer cycle;

    @PositiveOrZero(message = "휴식 시간은 0 이상이어야 합니다")
    @Schema(description = "휴식 시간 (초)")
    private Integer restTime;

    @PositiveOrZero(message = "인터벌은 0 이상이어야 합니다")
    @Schema(description = "인터벌 (초)")
    private Integer interval;

    @Positive(message = "인원은 0보다 커야 합니다")
    @Schema(description = "인원")
    private Integer personnel;

    @PositiveOrZero(message = "순서는 0 이상이어야 합니다")
    @Schema(description = "순서 인덱스")
    private Integer orderIndex;

    // Constructors
    public TrainingDetailRequest() {
    }

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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }

    public Integer getRestTime() {
        return restTime;
    }

    public void setRestTime(Integer restTime) {
        this.restTime = restTime;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Integer personnel) {
        this.personnel = personnel;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}

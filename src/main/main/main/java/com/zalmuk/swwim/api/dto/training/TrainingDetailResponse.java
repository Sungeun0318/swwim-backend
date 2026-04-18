package com.zalmuk.swwim.api.dto.training;

import com.zalmuk.swwim.api.entity.training.TrainingDetail;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * 훈련 상세 응답 DTO
 */
@Schema(description = "훈련 상세 응답")
public class TrainingDetailResponse {

    @Schema(description = "상세 ID")
    private UUID id;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "거리 (미터)")
    private Integer distance;

    @Schema(description = "횟수")
    private Integer count;

    @Schema(description = "사이클")
    private Integer cycle;

    @Schema(description = "휴식 시간 (초)")
    private Integer restTime;

    @Schema(description = "인터벌 (초)")
    private Integer interval;

    @Schema(description = "인원")
    private Integer personnel;

    @Schema(description = "순서 인덱스")
    private Integer orderIndex;

    // Constructors
    public TrainingDetailResponse() {
    }

    public static TrainingDetailResponse from(TrainingDetail detail) {
        TrainingDetailResponse response = new TrainingDetailResponse();
        response.setId(detail.getId());
        response.setTitle(detail.getTitle());
        response.setDistance(detail.getDistance());
        response.setCount(detail.getCount());
        response.setCycle(detail.getCycle());
        response.setRestTime(detail.getRestTime());
        response.setInterval(detail.getInterval());
        response.setPersonnel(detail.getPersonnel());
        response.setOrderIndex(detail.getOrderIndex());
        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

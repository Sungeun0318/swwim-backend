package com.zalmuk.swwim.api.dto.training;

import com.zalmuk.swwim.api.entity.training.TrainingResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 훈련 결과 응답 DTO
 */
@Schema(description = "훈련 결과 응답")
public class TrainingResultResponse {

    @Schema(description = "결과 ID")
    private UUID id;

    @Schema(description = "세션 ID")
    private UUID sessionId;

    @Schema(description = "사용자 ID")
    private String userId;

    @Schema(description = "총 시간 (HH:mm:ss)")
    private String totalTime;

    @Schema(description = "총 거리 (미터)")
    private Integer totalDistance;

    @Schema(description = "훈련 제목")
    private String sessionTitle;

    @Schema(description = "완료 시간")
    private LocalDateTime completedAt;

    // Constructors
    public TrainingResultResponse() {
    }

    public static TrainingResultResponse from(TrainingResult result) {
        TrainingResultResponse response = new TrainingResultResponse();
        response.setId(result.getId());
        response.setSessionId(result.getSession().getId());
        response.setUserId(result.getUser().getId());
        response.setTotalTime(result.getTotalTime());
        response.setTotalDistance(result.getTotalDistance());
        response.setSessionTitle(result.getSession().getTitle());
        response.setCompletedAt(result.getCompletedAt());
        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}

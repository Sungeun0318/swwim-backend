package com.zalmuk.swwim.api.dto.training;

import com.zalmuk.swwim.api.dto.user.UserResponse;
import com.zalmuk.swwim.api.entity.enums.TrainingStatus;
import com.zalmuk.swwim.api.entity.training.TrainingSession;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 훈련 세션 응답 DTO
 */
@Schema(description = "훈련 세션 응답")
public class TrainingSessionResponse {

    @Schema(description = "세션 ID")
    private UUID id;

    @Schema(description = "사용자 정보")
    private UserResponse user;

    @Schema(description = "훈련 제목")
    private String title;

    @Schema(description = "비프음")
    private String beepSound;

    @Schema(description = "인원 수")
    private Integer numPeople;

    @Schema(description = "총 시간 (초)")
    private Integer totalTime;

    @Schema(description = "총 거리 (미터)")
    private Integer totalDistance;

    @Schema(description = "상태")
    private TrainingStatus status;

    @Schema(description = "완료 여부")
    private Boolean isCompleted;

    @Schema(description = "메타데이터")
    private Map<String, Object> metadata;

    @Schema(description = "완료 시간")
    private LocalDateTime completedAt;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "훈련 상세 목록")
    private List<TrainingDetailResponse> details;

    // Constructors
    public TrainingSessionResponse() {
    }

    public static TrainingSessionResponse from(TrainingSession session) {
        TrainingSessionResponse response = new TrainingSessionResponse();
        response.setId(session.getId());
        response.setUser(UserResponse.summary(session.getUser()));
        response.setTitle(session.getTitle());
        response.setBeepSound(session.getBeepSound());
        response.setNumPeople(session.getNumPeople());
        response.setTotalTime(session.getTotalTime());
        response.setTotalDistance(session.getTotalDistance());
        response.setStatus(session.getStatus());
        response.setIsCompleted(session.getIsCompleted());
        response.setMetadata(session.getMetadata());
        response.setCompletedAt(session.getCompletedAt());
        response.setCreatedAt(session.getCreatedAt());

        if (session.getDetails() != null && !session.getDetails().isEmpty()) {
            response.setDetails(session.getDetails().stream()
                    .map(TrainingDetailResponse::from)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    public static TrainingSessionResponse summary(TrainingSession session) {
        TrainingSessionResponse response = new TrainingSessionResponse();
        response.setId(session.getId());
        response.setTitle(session.getTitle());
        response.setTotalTime(session.getTotalTime());
        response.setTotalDistance(session.getTotalDistance());
        response.setStatus(session.getStatus());
        response.setIsCompleted(session.getIsCompleted());
        response.setCreatedAt(session.getCreatedAt());
        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

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

    public TrainingStatus getStatus() {
        return status;
    }

    public void setStatus(TrainingStatus status) {
        this.status = status;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<TrainingDetailResponse> getDetails() {
        return details;
    }

    public void setDetails(List<TrainingDetailResponse> details) {
        this.details = details;
    }
}

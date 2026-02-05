package com.zalmuk.swwim.api.dto.training;

import com.zalmuk.swwim.api.dto.user.UserResponse;
import com.zalmuk.swwim.api.entity.training.TrainingTemplate;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 훈련 템플릿 응답 DTO
 */
@Schema(description = "훈련 템플릿 응답")
public class TrainingTemplateResponse {

    @Schema(description = "템플릿 ID")
    private UUID id;

    @Schema(description = "작성자 정보")
    private UserResponse author;

    @Schema(description = "템플릿 이름")
    private String name;

    @Schema(description = "템플릿 설명")
    private String description;

    @Schema(description = "공개 여부")
    private Boolean isPublic;

    @Schema(description = "사용 횟수")
    private Integer useCount;

    @Schema(description = "템플릿 데이터 (JSON)")
    private Map<String, Object> templateData;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    // Constructors
    public TrainingTemplateResponse() {
    }

    public static TrainingTemplateResponse from(TrainingTemplate template) {
        TrainingTemplateResponse response = new TrainingTemplateResponse();
        response.setId(template.getId());
        response.setAuthor(UserResponse.summary(template.getUser()));
        response.setName(template.getName());
        response.setDescription(template.getDescription());
        response.setIsPublic(template.getIsPublic());
        response.setUseCount(template.getUseCount());
        response.setTemplateData(template.getTemplateData());
        response.setCreatedAt(template.getCreatedAt());
        response.setUpdatedAt(template.getUpdatedAt());
        return response;
    }

    public static TrainingTemplateResponse summary(TrainingTemplate template) {
        TrainingTemplateResponse response = new TrainingTemplateResponse();
        response.setId(template.getId());
        response.setName(template.getName());
        response.setUseCount(template.getUseCount());
        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserResponse getAuthor() {
        return author;
    }

    public void setAuthor(UserResponse author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Map<String, Object> getTemplateData() {
        return templateData;
    }

    public void setTemplateData(Map<String, Object> templateData) {
        this.templateData = templateData;
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

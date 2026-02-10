package com.zalmuk.swwim.api.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

/**
 * 훈련 템플릿 요청 DTO
 */
@Schema(description = "훈련 템플릿 요청")
public class TrainingTemplateRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 200, message = "이름은 200자를 초과할 수 없습니다")
    @Schema(description = "템플릿 이름", required = true)
    private String name;

    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
    @Schema(description = "템플릿 설명")
    private String description;

    @Schema(description = "공개 여부", defaultValue = "false")
    private Boolean isPublic = false;

    @NotBlank(message = "비프음은 필수입니다")
    @Schema(description = "비프음", required = true)
    private String beepSound;

    @NotNull(message = "총 시간은 필수입니다")
    @Schema(description = "총 시간 (초)", required = true)
    private Integer totalTime;

    @NotNull(message = "총 거리는 필수입니다")
    @Schema(description = "총 거리 (미터)", required = true)
    private Integer totalDistance;

    @Schema(description = "템플릿 데이터 (JSON)")
    private Map<String, Object> templateData;

    @Valid
    @Schema(description = "훈련 상세 목록")
    private List<TrainingDetailRequest> details;

    // Constructors
    public TrainingTemplateRequest() {
    }

    // Getters and Setters
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

    public String getBeepSound() {
        return beepSound;
    }

    public void setBeepSound(String beepSound) {
        this.beepSound = beepSound;
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

    public Map<String, Object> getTemplateData() {
        return templateData;
    }

    public void setTemplateData(Map<String, Object> templateData) {
        this.templateData = templateData;
    }

    public List<TrainingDetailRequest> getDetails() {
        return details;
    }

    public void setDetails(List<TrainingDetailRequest> details) {
        this.details = details;
    }
}

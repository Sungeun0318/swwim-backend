package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.dto.common.PageResponse;
import com.zalmuk.swwim.api.dto.training.TrainingTemplateRequest;
import com.zalmuk.swwim.api.dto.training.TrainingTemplateResponse;
import com.zalmuk.swwim.api.entity.training.TrainingTemplate;
import com.zalmuk.swwim.api.service.training.TrainingTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 훈련 템플릿 컨트롤러
 */
@Tag(name = "Training Template", description = "훈련 템플릿 API")
@RestController
@RequestMapping("/api/v1/templates")
public class TrainingTemplateController {

    private final TrainingTemplateService templateService;

    public TrainingTemplateController(TrainingTemplateService templateService) {
        this.templateService = templateService;
    }

    @Operation(summary = "내 템플릿 목록", description = "로그인한 사용자의 템플릿 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TrainingTemplateResponse>>> getMyTemplates(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrainingTemplate> templates = templateService.getUserTemplates(userId, pageable);
        Page<TrainingTemplateResponse> responses = templates.map(TrainingTemplateResponse::from);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "템플릿 생성", description = "새로운 훈련 템플릿을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<TrainingTemplateResponse>> createTemplate(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody TrainingTemplateRequest request) {
        TrainingTemplate template = templateService.createTemplate(
                userId,
                request.getName(),
                request.getDescription(),
                request.getTemplateData(),
                request.getIsPublic() != null && request.getIsPublic()
        );
        return ResponseEntity.ok(ApiResponse.success(TrainingTemplateResponse.from(template), "템플릿이 생성되었습니다."));
    }

    @Operation(summary = "공개 템플릿 목록", description = "공개된 템플릿 목록을 조회합니다.")
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<PageResponse<TrainingTemplateResponse>>> getPublicTemplates(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrainingTemplate> templates = templateService.getPublicTemplates(pageable);
        Page<TrainingTemplateResponse> responses = templates.map(TrainingTemplateResponse::summary);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "검색 가능한 템플릿 목록", description = "사용자가 사용 가능한 템플릿 목록을 조회합니다 (본인 + 공개).")
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<PageResponse<TrainingTemplateResponse>>> getAvailableTemplates(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrainingTemplate> templates = templateService.getAvailableTemplates(userId, pageable);
        Page<TrainingTemplateResponse> responses = templates.map(TrainingTemplateResponse::summary);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "템플릿 상세 조회", description = "특정 템플릿의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TrainingTemplateResponse>> getTemplate(
            @PathVariable UUID id) {
        return templateService.findById(id)
                .map(template -> ResponseEntity.ok(ApiResponse.success(TrainingTemplateResponse.from(template))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "템플릿 수정", description = "템플릿을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TrainingTemplateResponse>> updateTemplate(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @Valid @RequestBody TrainingTemplateRequest request) {
        TrainingTemplate template = templateService.updateTemplate(
                id,
                userId,
                request.getName(),
                request.getDescription(),
                request.getTemplateData(),
                request.getIsPublic() != null && request.getIsPublic()
        );
        return ResponseEntity.ok(ApiResponse.success(TrainingTemplateResponse.from(template), "템플릿이 수정되었습니다."));
    }

    @Operation(summary = "템플릿 삭제", description = "템플릿을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        templateService.deleteTemplate(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "템플릿이 삭제되었습니다."));
    }

    @Operation(summary = "템플릿 사용", description = "템플릿을 사용하여 훈련을 생성할 때 호출합니다.")
    @PostMapping("/{id}/use")
    public ResponseEntity<ApiResponse<Void>> useTemplate(
            @PathVariable UUID id) {
        templateService.incrementUseCount(id);
        return ResponseEntity.ok(ApiResponse.success(null, "템플릿 사용 횟수가 증가했습니다."));
    }
}

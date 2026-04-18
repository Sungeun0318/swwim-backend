package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.entity.enums.UserLevel;
import com.zalmuk.swwim.api.entity.quickstart.QuickstartDailyCompletion;
import com.zalmuk.swwim.api.entity.quickstart.QuickstartProgress;
import com.zalmuk.swwim.api.service.quickstart.QuickstartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 퀵스타트 컨트롤러
 */
@Tag(name = "Quickstart", description = "퀵스타트 프로그램 API")
@RestController
@RequestMapping("/api/v1/quickstart")
public class QuickstartController {

    private final QuickstartService quickstartService;

    public QuickstartController(QuickstartService quickstartService) {
        this.quickstartService = quickstartService;
    }

    @Operation(summary = "전체 진행 상황 조회")
    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<List<QuickstartProgress>>> getAllProgress(
            @AuthenticationPrincipal String userId) {
        List<QuickstartProgress> progresses = quickstartService.getAllProgress(userId);
        return ResponseEntity.ok(ApiResponse.success(progresses));
    }

    @Operation(summary = "레벨별 진행 상황 조회")
    @GetMapping("/progress/{level}")
    public ResponseEntity<ApiResponse<QuickstartProgress>> getProgress(
            @AuthenticationPrincipal String userId,
            @PathVariable String level) {
        UserLevel userLevel = UserLevel.valueOf(level.toUpperCase());
        return quickstartService.getProgress(userId, userLevel)
                .map(progress -> ResponseEntity.ok(ApiResponse.success(progress)))
                .orElse(ResponseEntity.ok(ApiResponse.success(null)));
    }

    @Operation(summary = "프로그램 시작")
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<QuickstartProgress>> startProgram(
            @AuthenticationPrincipal String userId,
            @RequestBody Map<String, String> body) {
        UserLevel level = UserLevel.valueOf(body.get("level").toUpperCase());
        QuickstartProgress progress = quickstartService.startProgram(userId, level);
        return ResponseEntity.ok(ApiResponse.success(progress, "프로그램이 시작되었습니다."));
    }

    @Operation(summary = "일일 훈련 완료")
    @PostMapping("/{progressId}/complete")
    public ResponseEntity<ApiResponse<QuickstartDailyCompletion>> completeDailyTraining(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID progressId,
            @RequestBody Map<String, Object> body) {
        int week = (Integer) body.get("week");
        int totalTime = body.containsKey("totalTime") ? (Integer) body.get("totalTime") : 0;
        int totalDistance = body.containsKey("totalDistance") ? (Integer) body.get("totalDistance") : 0;
        QuickstartDailyCompletion completion = quickstartService.completeDailyTraining(
                progressId, week, totalTime, totalDistance);
        return ResponseEntity.ok(ApiResponse.success(completion, "훈련이 완료되었습니다."));
    }

    @Operation(summary = "진행 상황 삭제")
    @DeleteMapping("/progress/{progressId}")
    public ResponseEntity<ApiResponse<Void>> deleteProgress(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID progressId) {
        quickstartService.deleteProgress(progressId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "진행 상황이 삭제되었습니다."));
    }
}

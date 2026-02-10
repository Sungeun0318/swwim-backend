package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.dto.common.PageResponse;
import com.zalmuk.swwim.api.dto.training.*;
import com.zalmuk.swwim.api.entity.training.TrainingSession;
import com.zalmuk.swwim.api.service.training.TrainingService;
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
 * 훈련 컨트롤러
 */
@Tag(name = "Training", description = "훈련 API")
@RestController
@RequestMapping("/api/v1/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @Operation(summary = "훈련 목록 조회", description = "로그인한 사용자의 훈련 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TrainingSessionResponse>>> getTrainings(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrainingSession> sessions = trainingService.getUserSessions(userId, pageable);
        Page<TrainingSessionResponse> responses = sessions.map(TrainingSessionResponse::summary);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "훈련 생성", description = "새로운 훈련 세션을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<TrainingSessionResponse>> createTraining(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody TrainingSessionRequest request) {
        TrainingSession session = trainingService.createSession(
                userId,
                request.getTitle(),
                request.getBeepSound(),
                request.getNumPeople(),
                request.getTotalTime(),
                request.getTotalDistance()
        );

        // 상세 항목 추가
        if (request.getDetails() != null) {
            for (int i = 0; i < request.getDetails().size(); i++) {
                TrainingDetailRequest detail = request.getDetails().get(i);
                trainingService.addDetail(
                        session.getId(),
                        detail.getTitle(),
                        detail.getDistance(),
                        detail.getCount(),
                        detail.getCycle(),
                        detail.getRestTime(),
                        detail.getInterval(),
                        detail.getPersonnel(),
                        detail.getOrderIndex() != null ? detail.getOrderIndex() : i
                );
            }
        }

        // 저장된 세션 다시 조회 (상세 포함)
        TrainingSession createdSession = trainingService.findSessionById(session.getId()).orElse(session);
        return ResponseEntity.ok(ApiResponse.success(TrainingSessionResponse.from(createdSession), "훈련이 생성되었습니다."));
    }

    @Operation(summary = "훈련 상세 조회", description = "특정 훈련 세션의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TrainingSessionResponse>> getTraining(
            @PathVariable UUID id) {
        return trainingService.findSessionById(id)
                .map(session -> ResponseEntity.ok(ApiResponse.success(TrainingSessionResponse.from(session))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "훈련 삭제", description = "훈련 세션을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTraining(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        trainingService.deleteSession(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "훈련이 삭제되었습니다."));
    }

    @Operation(summary = "훈련 완료", description = "훈련 세션을 완료 처리하고 결과를 저장합니다.")
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<TrainingResultResponse>> completeTraining(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @RequestBody(required = false) TrainingCompleteRequest request) {
        String totalTime = "00:00:00";
        Integer totalDistance = 0;
        if (request != null) {
            totalTime = request.getTotalTimeAsString();
            if (request.getTotalDistance() != null) totalDistance = request.getTotalDistance();
        }
        var result = trainingService.completeSession(id, totalTime, totalDistance, null);
        return ResponseEntity.ok(ApiResponse.success(TrainingResultResponse.from(result), "훈련이 완료되었습니다."));
    }

    @Operation(summary = "완료된 훈련 목록", description = "완료된 훈련 세션 목록을 조회합니다.")
    @GetMapping("/completed")
    public ResponseEntity<ApiResponse<PageResponse<TrainingSessionResponse>>> getCompletedTrainings(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrainingSession> sessions = trainingService.getCompletedSessions(userId, pageable);
        Page<TrainingSessionResponse> responses = sessions.map(TrainingSessionResponse::summary);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "훈련 결과 목록", description = "훈련 결과 목록을 조회합니다.")
    @GetMapping("/results")
    public ResponseEntity<ApiResponse<PageResponse<TrainingResultResponse>>> getResults(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        var results = trainingService.getUserResults(userId, pageable);
        Page<TrainingResultResponse> responses = results.map(TrainingResultResponse::from);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }
}

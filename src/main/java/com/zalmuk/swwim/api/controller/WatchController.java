package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.dto.watch.WatchWorkoutLinkRequest;
import com.zalmuk.swwim.api.dto.watch.WatchWorkoutRequest;
import com.zalmuk.swwim.api.dto.watch.WatchWorkoutResponse;
import com.zalmuk.swwim.api.entity.training.WatchWorkout;
import com.zalmuk.swwim.api.service.training.WatchWorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Watch", description = "워치 운동 기록 API")
@RestController
@RequestMapping("/api/v1/watch-workouts")
public class WatchController {

    private final WatchWorkoutService watchWorkoutService;

    public WatchController(WatchWorkoutService watchWorkoutService) {
        this.watchWorkoutService = watchWorkoutService;
    }

    @Operation(summary = "워치 운동 기록 업서트")
    @PostMapping
    public ResponseEntity<ApiResponse<WatchWorkoutResponse>> upsertWorkout(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody WatchWorkoutRequest request) {
        WatchWorkout workout = watchWorkoutService.upsertWorkout(userId, request);
        return ResponseEntity.ok(ApiResponse.success(WatchWorkoutResponse.from(workout), "워치 운동 기록이 저장되었습니다."));
    }

    @Operation(summary = "워치 운동 기록을 캘린더 이벤트에 연결")
    @PostMapping("/{id}/link")
    public ResponseEntity<ApiResponse<WatchWorkoutResponse>> linkWorkout(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @Valid @RequestBody WatchWorkoutLinkRequest request) {
        WatchWorkout workout = watchWorkoutService.linkToCalendarEvent(
                userId,
                id,
                request.getCalendarEventId(),
                request.getMatchConfidence());
        return ResponseEntity.ok(ApiResponse.success(WatchWorkoutResponse.from(workout), "워치 운동 기록이 연결되었습니다."));
    }

    @Operation(summary = "워치 운동 기록 연결 해제")
    @DeleteMapping("/{id}/link")
    public ResponseEntity<ApiResponse<WatchWorkoutResponse>> unlinkWorkout(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        WatchWorkout workout = watchWorkoutService.unlink(userId, id);
        return ResponseEntity.ok(ApiResponse.success(WatchWorkoutResponse.from(workout), "워치 운동 기록 연결이 해제되었습니다."));
    }
}

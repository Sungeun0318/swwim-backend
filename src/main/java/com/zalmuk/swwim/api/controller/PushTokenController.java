package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.dto.notification.PushTokenRequest;
import com.zalmuk.swwim.api.dto.notification.PushTokenResponse;
import com.zalmuk.swwim.api.entity.notification.UserPushToken;
import com.zalmuk.swwim.api.service.notification.PushTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Push Token", description = "사용자 푸시 토큰 API")
@RestController
@RequestMapping("/api/v1/me/push-tokens")
public class PushTokenController {

    private final PushTokenService pushTokenService;

    public PushTokenController(PushTokenService pushTokenService) {
        this.pushTokenService = pushTokenService;
    }

    @Operation(summary = "푸시 토큰 목록", description = "JWT sub에 해당하는 사용자의 푸시 토큰 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PushTokenResponse>>> getTokens(
            @AuthenticationPrincipal String userId) {
        List<PushTokenResponse> responses = pushTokenService.getTokens(userId).stream()
                .map(PushTokenResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "푸시 토큰 등록", description = "푸시 토큰을 등록하거나 동일 토큰의 마지막 사용 시각과 활성 상태를 갱신합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PushTokenResponse>> upsertToken(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody PushTokenRequest request) {
        UserPushToken token = pushTokenService.upsert(userId, request);
        return ResponseEntity.ok(ApiResponse.success(PushTokenResponse.from(token), "푸시 토큰이 등록되었습니다."));
    }

    @Operation(summary = "푸시 토큰 삭제", description = "JWT sub 소유의 특정 푸시 토큰을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteToken(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        pushTokenService.deleteById(userId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "푸시 토큰이 삭제되었습니다."));
    }

    @Operation(summary = "디바이스 푸시 토큰 삭제", description = "JWT sub 소유의 특정 디바이스 푸시 토큰을 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Integer>> deleteDeviceTokens(
            @AuthenticationPrincipal String userId,
            @RequestParam("device") String deviceId) {
        int deleted = pushTokenService.deleteByDevice(userId, deviceId);
        return ResponseEntity.ok(ApiResponse.success(deleted, "디바이스 푸시 토큰이 삭제되었습니다."));
    }
}

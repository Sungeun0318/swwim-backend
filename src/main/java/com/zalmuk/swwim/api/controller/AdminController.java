package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.entity.user.UserEntitlement;
import com.zalmuk.swwim.api.service.user.EntitlementService;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 관리자 전용 컨트롤러
 * SecurityConfig에서 /api/v1/admin/** 은 ROLE_ADMIN만 접근 가능
 */
@Tag(name = "Admin", description = "관리자 API")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EntitlementService entitlementService;
    private final UserRepository userRepository;

    @Operation(summary = "유저 entitlement 조회")
    @GetMapping("/users/{userId}/entitlements")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUserEntitlements(
            @PathVariable String userId) {
        List<UserEntitlement> entitlements = entitlementService.getAllEntitlements(userId);
        List<Map<String, Object>> result = entitlements.stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "유저 프리미엄 여부 확인")
    @GetMapping("/users/{userId}/premium")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkPremium(
            @PathVariable String userId) {
        boolean isPremium = entitlementService.isPremium(userId);
        List<UserEntitlement> active = entitlementService.getActiveEntitlements(userId);
        List<String> entitlementNames = active.stream()
                .map(UserEntitlement::getEntitlement)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "userId", userId,
                "isPremium", isPremium,
                "activeEntitlements", entitlementNames
        )));
    }

    @Operation(summary = "유저에게 entitlement 부여")
    @PostMapping("/users/{userId}/entitlements")
    public ResponseEntity<ApiResponse<Map<String, Object>>> grantEntitlement(
            @AuthenticationPrincipal String adminId,
            @PathVariable String userId,
            @RequestBody Map<String, Object> body) {

        String entitlement = (String) body.get("entitlement");
        if (entitlement == null || entitlement.isBlank()) {
            throw new IllegalArgumentException("entitlement 값이 필요합니다.");
        }

        // 만료일 (선택)
        LocalDateTime expiresAt = null;
        if (body.get("expiresAt") != null) {
            expiresAt = LocalDateTime.parse((String) body.get("expiresAt"));
        }

        UserEntitlement granted = entitlementService.grantEntitlement(
                userId, entitlement, adminId, expiresAt);

        return ResponseEntity.ok(ApiResponse.success(toMap(granted),
                "'" + entitlement + "' entitlement이 부여되었습니다."));
    }

    @Operation(summary = "유저 entitlement 취소")
    @DeleteMapping("/users/{userId}/entitlements/{entitlement}")
    public ResponseEntity<ApiResponse<Void>> revokeEntitlement(
            @PathVariable String userId,
            @PathVariable String entitlement) {
        entitlementService.revokeEntitlement(userId, entitlement);
        return ResponseEntity.ok(ApiResponse.success(null,
                "'" + entitlement + "' entitlement이 취소되었습니다."));
    }

    @Operation(summary = "유저 관리자 권한 토글")
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleAdmin(
            @AuthenticationPrincipal String adminId,
            @PathVariable String userId,
            @RequestBody Map<String, Object> body) {

        Boolean isAdmin = (Boolean) body.get("isAdmin");
        if (isAdmin == null) {
            throw new IllegalArgumentException("isAdmin 값이 필요합니다.");
        }

        // 자기 자신 관리자 해제 방지
        if (adminId.equals(userId) && !isAdmin) {
            throw new IllegalArgumentException("자기 자신의 관리자 권한은 해제할 수 없습니다.");
        }

        userRepository.findById(userId).ifPresent(user -> {
            user.setIsAdmin(isAdmin);
            userRepository.save(user);
        });

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "userId", userId,
                "isAdmin", isAdmin
        ), isAdmin ? "관리자 권한이 부여되었습니다." : "관리자 권한이 해제되었습니다."));
    }

    private Map<String, Object> toMap(UserEntitlement e) {
        return Map.of(
                "id", e.getId().toString(),
                "entitlement", e.getEntitlement(),
                "source", e.getSource().name(),
                "grantedBy", e.getGrantedBy() != null ? e.getGrantedBy() : "",
                "expiresAt", e.getExpiresAt() != null ? e.getExpiresAt().toString() : "permanent",
                "isActive", e.isActive(),
                "createdAt", e.getCreatedAt().toString()
        );
    }
}

package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.dto.common.PageResponse;
import com.zalmuk.swwim.api.dto.notification.NotificationResponse;
import com.zalmuk.swwim.api.entity.notification.Notification;
import com.zalmuk.swwim.api.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 알림 컨트롤러
 */
@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "알림 목록", description = "로그인한 사용자의 알림 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Notification> notifications = notificationService.getUserNotifications(userId, pageable);
        Page<NotificationResponse> responses = notifications.map(NotificationResponse::from);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "읽지 않은 알림 개수", description = "읽지 않은 알림 개수를 조회합니다.")
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal String userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(null, "읽음 처리되었습니다."));
    }

    @Operation(summary = "전체 읽음 처리", description = "모든 알림을 읽음 처리합니다.")
    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "모든 알림이 읽음 처리되었습니다."));
    }

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "알림이 삭제되었습니다."));
    }

    @Operation(summary = "읽지 않은 알림 목록", description = "읽지 않은 알림 목록을 조회합니다.")
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getUnreadNotifications(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Notification> notifications = notificationService.getUnreadNotifications(userId, pageable);
        Page<NotificationResponse> responses = notifications.map(NotificationResponse::from);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }
}

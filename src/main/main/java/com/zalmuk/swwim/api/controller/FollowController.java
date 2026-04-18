package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.dto.common.PageResponse;
import com.zalmuk.swwim.api.dto.user.UserResponse;
import com.zalmuk.swwim.api.service.user.BlockService;
import com.zalmuk.swwim.api.service.user.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 팔로우/차단 컨트롤러
 */
@Tag(name = "Follow", description = "팔로우/차단 API")
@RestController
@RequestMapping("/api/v1/users")
public class FollowController {

    private final FollowService followService;
    private final BlockService blockService;

    public FollowController(FollowService followService, BlockService blockService) {
        this.followService = followService;
        this.blockService = blockService;
    }

    @Operation(summary = "팔로우", description = "사용자를 팔로우합니다.")
    @PostMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<Void>> follow(
            @AuthenticationPrincipal String myUserId,
            @PathVariable String userId) {
        followService.follow(myUserId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "팔로우했습니다."));
    }

    @Operation(summary = "언팔로우", description = "사용자를 언팔로우합니다.")
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @AuthenticationPrincipal String myUserId,
            @PathVariable String userId) {
        followService.unfollow(myUserId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "언팔로우했습니다."));
    }

    @Operation(summary = "팔로워 목록", description = "사용자의 팔로워 목록을 조회합니다.")
    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getFollowers(
            @PathVariable String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponse> followers = followService.getFollowers(userId, pageable)
                .map(UserResponse::summary);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(followers)));
    }

    @Operation(summary = "팔로잉 목록", description = "사용자의 팔로잉 목록을 조회합니다.")
    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getFollowing(
            @PathVariable String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponse> following = followService.getFollowing(userId, pageable)
                .map(UserResponse::summary);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(following)));
    }

    @Operation(summary = "팔로우 여부 확인", description = "특정 사용자를 팔로우하고 있는지 확인합니다.")
    @GetMapping("/{userId}/follow/status")
    public ResponseEntity<ApiResponse<Boolean>> isFollowing(
            @AuthenticationPrincipal String myUserId,
            @PathVariable String userId) {
        boolean isFollowing = followService.isFollowing(myUserId, userId);
        return ResponseEntity.ok(ApiResponse.success(isFollowing));
    }

    @Operation(summary = "차단", description = "사용자를 차단합니다.")
    @PostMapping("/{userId}/block")
    public ResponseEntity<ApiResponse<Void>> block(
            @AuthenticationPrincipal String myUserId,
            @PathVariable String userId) {
        blockService.blockUser(myUserId, userId, null);
        return ResponseEntity.ok(ApiResponse.success(null, "차단했습니다."));
    }

    @Operation(summary = "차단 해제", description = "사용자 차단을 해제합니다.")
    @DeleteMapping("/{userId}/block")
    public ResponseEntity<ApiResponse<Void>> unblock(
            @AuthenticationPrincipal String myUserId,
            @PathVariable String userId) {
        blockService.unblockUser(myUserId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "차단을 해제했습니다."));
    }

    @Operation(summary = "차단 여부 확인", description = "특정 사용자를 차단했는지 확인합니다.")
    @GetMapping("/{userId}/block/status")
    public ResponseEntity<ApiResponse<Boolean>> isBlocked(
            @AuthenticationPrincipal String myUserId,
            @PathVariable String userId) {
        boolean isBlocked = blockService.isBlocked(myUserId, userId);
        return ResponseEntity.ok(ApiResponse.success(isBlocked));
    }

    @Operation(summary = "차단 목록", description = "내가 차단한 사용자 목록을 조회합니다.")
    @GetMapping("/me/blocked")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getBlockedUsers(
            @AuthenticationPrincipal String myUserId) {
        List<UserResponse> blockedUsers = blockService.getBlockedUsers(myUserId).stream()
                .map(UserResponse::summary)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(blockedUsers));
    }
}

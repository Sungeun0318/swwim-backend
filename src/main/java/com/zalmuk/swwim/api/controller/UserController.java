package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.dto.common.PageResponse;
import com.zalmuk.swwim.api.dto.user.*;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.entity.user.UserSettings;
import com.zalmuk.swwim.api.entity.user.UserStats;
import com.zalmuk.swwim.api.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 사용자 컨트롤러
 */
@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(
            @AuthenticationPrincipal String userId) {
        return userService.findById(userId)
                .map(user -> ResponseEntity.ok(ApiResponse.success(UserResponse.from(user))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "프로필 수정", description = "로그인한 사용자의 프로필을 수정합니다.")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        return userService.findById(userId)
                .map(user -> {
                    if (request.getName() != null) user.setName(request.getName());
                    if (request.getNickname() != null) user.setNickname(request.getNickname());
                    if (request.getProfileImageUrl() != null) user.setProfileImageUrl(request.getProfileImageUrl());
                    if (request.getBio() != null) user.setBio(request.getBio());
                    if (request.getLevel() != null) user.setLevel(request.getLevel());
                    if (request.getSwimStyle() != null) user.setSwimStyle(request.getSwimStyle());
                    if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
                    User updatedUser = userService.updateUser(user);
                    return ResponseEntity.ok(ApiResponse.success(UserResponse.from(updatedUser), "프로필이 수정되었습니다."));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "프로필 이미지 업로드", description = "프로필 이미지를 업로드합니다.")
    @PostMapping("/me/profile-image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfileImage(
            @AuthenticationPrincipal String userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = userService.uploadProfileImage(userId, file);
            return ResponseEntity.ok(ApiResponse.success(Map.of("imageUrl", imageUrl), "이미지가 업로드되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("이미지 업로드에 실패했습니다: " + e.getMessage()));
        }
    }

    @Operation(summary = "설정 조회", description = "로그인한 사용자의 설정을 조회합니다.")
    @GetMapping("/me/settings")
    public ResponseEntity<ApiResponse<UserSettingsResponse>> getSettings(
            @AuthenticationPrincipal String userId) {
        return userService.getSettings(userId)
                .map(settings -> ResponseEntity.ok(ApiResponse.success(UserSettingsResponse.from(settings))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "설정 수정", description = "로그인한 사용자의 설정을 수정합니다.")
    @PutMapping("/me/settings")
    public ResponseEntity<ApiResponse<UserSettingsResponse>> updateSettings(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody UserSettingsRequest request) {
        return userService.getSettings(userId)
                .map(settings -> {
                    if (request.getEnablePushNotifications() != null)
                        settings.setEnablePushNotifications(request.getEnablePushNotifications());
                    if (request.getEnableTrainingReminders() != null)
                        settings.setEnableTrainingReminders(request.getEnableTrainingReminders());
                    if (request.getReminderTime() != null)
                        settings.setReminderTime(request.getReminderTime());
                    if (request.getTheme() != null)
                        settings.setTheme(request.getTheme());
                    if (request.getLanguage() != null)
                        settings.setLanguage(request.getLanguage());
                    if (request.getDefaultBeepSound() != null)
                        settings.setDefaultBeepSound(request.getDefaultBeepSound());
                    if (request.getDefaultNumPeople() != null)
                        settings.setDefaultNumPeople(request.getDefaultNumPeople());
                    if (request.getAutoSaveTraining() != null)
                        settings.setAutoSaveTraining(request.getAutoSaveTraining());
                    if (request.getProfileVisibility() != null)
                        settings.setProfileVisibility(request.getProfileVisibility());
                    if (request.getShowStats() != null)
                        settings.setShowStats(request.getShowStats());
                    UserSettings updatedSettings = userService.updateSettings(settings);
                    return ResponseEntity.ok(ApiResponse.success(UserSettingsResponse.from(updatedSettings), "설정이 수정되었습니다."));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "통계 조회", description = "로그인한 사용자의 훈련 통계를 조회합니다.")
    @GetMapping("/me/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getStats(
            @AuthenticationPrincipal String userId) {
        return userService.getStats(userId)
                .map(stats -> ResponseEntity.ok(ApiResponse.success(UserStatsResponse.from(stats))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "사용자 조회", description = "특정 사용자의 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @PathVariable String userId) {
        return userService.findById(userId)
                .map(user -> ResponseEntity.ok(ApiResponse.success(UserResponse.from(user))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "사용자 검색", description = "이름 또는 닉네임으로 사용자를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(
            @Parameter(description = "검색 키워드") @RequestParam String keyword) {
        List<User> users = userService.searchUsers(keyword);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::summary)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}

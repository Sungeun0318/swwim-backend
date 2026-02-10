package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.auth.LoginRequest;
import com.zalmuk.swwim.api.dto.auth.LoginResponse;
import com.zalmuk.swwim.api.dto.auth.RegisterRequest;
import com.zalmuk.swwim.api.dto.auth.TokenRefreshRequest;
import com.zalmuk.swwim.api.dto.auth.TokenResponse;
import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러
 */
@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "소셜 로그인", description = "OAuth 토큰을 사용하여 소셜 로그인을 수행합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인 성공"));
    }

    @Operation(summary = "이메일 회원가입", description = "이메일과 비밀번호로 회원가입합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response, "회원가입 성공"));
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @Valid @RequestBody TokenRefreshRequest request) {
        TokenResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response, "토큰 갱신 성공"));
    }

    @Operation(summary = "로그아웃", description = "현재 세션을 종료합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody TokenRefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(null, "로그아웃 되었습니다."));
    }

    @Operation(summary = "전체 로그아웃", description = "모든 기기에서 로그아웃합니다.")
    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAll(
            @AuthenticationPrincipal String userId) {
        authService.logoutAll(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "모든 기기에서 로그아웃 되었습니다."));
    }
}

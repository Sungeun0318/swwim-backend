package com.zalmuk.swwim.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 소셜 로그인 요청 DTO
 */
@Schema(description = "로그인 요청")
public class LoginRequest {

    @NotBlank(message = "ID 토큰은 필수입니다")
    @Schema(description = "OAuth ID 토큰 또는 Access 토큰", required = true)
    private String idToken;

    @NotBlank(message = "인증 제공자는 필수입니다")
    @Schema(description = "인증 제공자 (google, apple, naver)", required = true, example = "google")
    private String provider;

    @Schema(description = "FCM 토큰 (푸시 알림용)")
    private String fcmToken;

    @Schema(description = "기기 정보")
    private String deviceInfo;

    // Constructors
    public LoginRequest() {
    }

    public LoginRequest(String idToken, String provider) {
        this.idToken = idToken;
        this.provider = provider;
    }

    // Getters and Setters
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}

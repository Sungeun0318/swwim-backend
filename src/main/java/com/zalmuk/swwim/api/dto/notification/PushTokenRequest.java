package com.zalmuk.swwim.api.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "푸시 토큰 등록 요청")
public class PushTokenRequest {

    @NotBlank
    @Size(max = 10)
    @Schema(description = "플랫폼", example = "ios", allowableValues = {"ios", "android"})
    private String platform;

    @NotBlank
    @Size(max = 10)
    @Schema(description = "푸시 제공자", example = "apns", allowableValues = {"apns", "fcm"})
    private String provider;

    @NotBlank
    @Schema(description = "APNs 또는 FCM 토큰")
    private String token;

    @Size(max = 128)
    @Schema(description = "디바이스 식별자")
    private String deviceId;

    @Size(max = 20)
    @Schema(description = "앱 버전", example = "1.0.4")
    private String appVersion;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}

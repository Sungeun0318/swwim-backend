package com.zalmuk.swwim.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 토큰 갱신 응답 DTO
 */
@Schema(description = "토큰 갱신 응답")
public class TokenResponse {

    @Schema(description = "액세스 토큰")
    private String accessToken;

    @Schema(description = "리프레시 토큰")
    private String refreshToken;

    @Schema(description = "토큰 타입")
    private String tokenType = "Bearer";

    @Schema(description = "토큰 만료 시간 (초)")
    private Long expiresIn;

    // Constructors
    public TokenResponse() {
    }

    public TokenResponse(String accessToken, String refreshToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}

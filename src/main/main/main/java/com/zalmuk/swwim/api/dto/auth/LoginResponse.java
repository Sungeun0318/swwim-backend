package com.zalmuk.swwim.api.dto.auth;

import com.zalmuk.swwim.api.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 로그인 응답 DTO
 */
@Schema(description = "로그인 응답")
public class LoginResponse {

    @Schema(description = "액세스 토큰")
    private String accessToken;

    @Schema(description = "리프레시 토큰")
    private String refreshToken;

    @Schema(description = "토큰 타입")
    private String tokenType = "Bearer";

    @Schema(description = "토큰 만료 시간 (초)")
    private Long expiresIn;

    @Schema(description = "사용자 정보")
    private UserResponse user;

    @Schema(description = "신규 가입 여부")
    private Boolean isNewUser;

    // Constructors
    public LoginResponse() {
    }

    public LoginResponse(String accessToken, String refreshToken, Long expiresIn, UserResponse user, Boolean isNewUser) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
        this.isNewUser = isNewUser;
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

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public Boolean getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(Boolean isNewUser) {
        this.isNewUser = isNewUser;
    }
}

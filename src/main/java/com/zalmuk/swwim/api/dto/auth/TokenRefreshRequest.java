package com.zalmuk.swwim.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 토큰 갱신 요청 DTO
 */
@Schema(description = "토큰 갱신 요청")
public class TokenRefreshRequest {

    @NotBlank(message = "리프레시 토큰은 필수입니다")
    @Schema(description = "리프레시 토큰", required = true)
    private String refreshToken;

    // Constructors
    public TokenRefreshRequest() {
    }

    public TokenRefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Getters and Setters
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

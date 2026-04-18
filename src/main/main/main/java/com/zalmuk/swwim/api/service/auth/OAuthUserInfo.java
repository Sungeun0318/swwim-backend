package com.zalmuk.swwim.api.service.auth;

import com.zalmuk.swwim.api.entity.enums.AuthProvider;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 사용자 정보
 */
@Getter
@Builder
public class OAuthUserInfo {
    private String id;           // 고유 ID (provider_providerId 형식)
    private String providerId;   // Provider별 고유 ID
    private AuthProvider provider;
    private String email;
    private String name;
    private String profileImageUrl;
}

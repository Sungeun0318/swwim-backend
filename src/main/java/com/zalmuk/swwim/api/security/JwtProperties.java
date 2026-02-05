package com.zalmuk.swwim.api.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 프로퍼티
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 시크릿 키 (256비트 이상)
     */
    private String secret;

    /**
     * Access Token 만료 시간 (밀리초)
     * 기본값: 1시간 (3600000ms)
     */
    private Long accessTokenExpiration = 3600000L;

    /**
     * Refresh Token 만료 시간 (밀리초)
     * 기본값: 30일 (2592000000ms)
     */
    private Long refreshTokenExpiration = 2592000000L;
}

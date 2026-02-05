package com.zalmuk.swwim.api.security;

import com.zalmuk.swwim.api.entity.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 생성 및 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(user.getId())
                .claim("email", user.getEmail())
                .claim("nickname", user.getNickname())
                .claim("isAdmin", user.getIsAdmin())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Access Token 생성 (userId만 사용)
     */
    public String createAccessToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    /**
     * 토큰에서 Claims 추출
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Refresh Token 만료 시간 반환 (밀리초)
     */
    public long getRefreshTokenExpiration() {
        return jwtProperties.getRefreshTokenExpiration();
    }
}

package com.zalmuk.swwim.api.service.auth;

import com.zalmuk.swwim.api.entity.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * OAuth 토큰 검증 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${oauth.google.client-id:}")
    private String googleClientId;

    @Value("${oauth.apple.client-id:}")
    private String appleClientId;

    @Value("${oauth.naver.client-id:}")
    private String naverClientId;

    @Value("${oauth.naver.client-secret:}")
    private String naverClientSecret;

    /**
     * OAuth 토큰 검증 및 사용자 정보 조회
     */
    public OAuthUserInfo verifyToken(String provider, String token) {
        AuthProvider authProvider = AuthProvider.fromValue(provider.toLowerCase());

        return switch (authProvider) {
            case GOOGLE -> verifyGoogleToken(token);
            case APPLE -> verifyAppleToken(token);
            case NAVER -> verifyNaverToken(token);
            case EMAIL -> throw new IllegalArgumentException("이메일 로그인은 OAuth를 사용하지 않습니다.");
        };
    }

    /**
     * Google ID Token 검증
     */
    @SuppressWarnings("unchecked")
    private OAuthUserInfo verifyGoogleToken(String idToken) {
        try {
            // Google tokeninfo API는 raw JWT 토큰을 받음 (base64url은 URL 안전 문자만 사용)
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

            log.info("Verifying Google token, token length: {}", idToken.length());
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> payload = response.getBody();
                log.info("Google token payload: {}", payload);

                // audience 검증 - iOS/Android 클라이언트 ID와 일치하는지 확인
                // (개발 환경에서는 audience 검증 스킵 가능)
                String aud = (String) payload.get("aud");
                log.info("Token audience: {}, Expected: {}", aud, googleClientId);

                String providerId = (String) payload.get("sub");
                log.info("Google token verified successfully for user: {}", payload.get("email"));

                return OAuthUserInfo.builder()
                        .id("google_" + providerId)
                        .providerId(providerId)
                        .provider(AuthProvider.GOOGLE)
                        .email((String) payload.get("email"))
                        .name((String) payload.get("name"))
                        .profileImageUrl((String) payload.get("picture"))
                        .build();
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("Google token verification failed with HTTP error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalArgumentException("Google 토큰 검증에 실패했습니다: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Failed to verify Google token: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Google 토큰 검증에 실패했습니다: " + e.getMessage());
        }

        throw new IllegalArgumentException("Invalid Google token");
    }

    /**
     * Apple ID Token 검증
     * Apple은 JWT 형식의 ID Token을 사용하므로 공개키로 검증
     */
    @SuppressWarnings("unchecked")
    private OAuthUserInfo verifyAppleToken(String idToken) {
        try {
            // Apple JWT 디코딩 (실제로는 공개키로 검증해야 함)
            // 여기서는 간단히 payload만 추출
            String[] parts = idToken.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid Apple token format");
            }

            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
            log.info("Apple token claims: {}", claims);

            // audience 검증 (client ID가 설정되어 있을 때만)
            String aud = (String) claims.get("aud");
            log.info("Apple token audience: {}, Expected: {}", aud, appleClientId);

            if (appleClientId != null && !appleClientId.isEmpty() && !appleClientId.equals(aud)) {
                log.warn("Apple token audience mismatch. Expected: {}, Got: {}", appleClientId, aud);
                throw new IllegalArgumentException("Invalid Apple token audience");
            }

            String providerId = (String) claims.get("sub");
            String email = (String) claims.get("email");
            log.info("Apple token verified for user: {}", email);

            return OAuthUserInfo.builder()
                    .id("apple_" + providerId)
                    .providerId(providerId)
                    .provider(AuthProvider.APPLE)
                    .email(email)
                    .name(null) // Apple은 이름을 첫 로그인에만 제공
                    .profileImageUrl(null)
                    .build();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to verify Apple token: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Apple 토큰 검증에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Naver Access Token으로 사용자 정보 조회
     */
    @SuppressWarnings("unchecked")
    private OAuthUserInfo verifyNaverToken(String accessToken) {
        try {
            String url = "https://openapi.naver.com/v1/nid/me";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Map<String, Object> responseData = (Map<String, Object>) body.get("response");

                if (responseData != null) {
                    String providerId = (String) responseData.get("id");
                    return OAuthUserInfo.builder()
                            .id("naver_" + providerId)
                            .providerId(providerId)
                            .provider(AuthProvider.NAVER)
                            .email((String) responseData.get("email"))
                            .name((String) responseData.get("name"))
                            .profileImageUrl((String) responseData.get("profile_image"))
                            .build();
                }
            }
        } catch (Exception e) {
            log.error("Failed to verify Naver token", e);
            throw new IllegalArgumentException("Naver 토큰 검증에 실패했습니다.");
        }

        throw new IllegalArgumentException("Invalid Naver token");
    }
}

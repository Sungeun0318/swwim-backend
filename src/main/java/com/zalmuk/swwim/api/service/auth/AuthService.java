package com.zalmuk.swwim.api.service.auth;

import com.zalmuk.swwim.api.dto.auth.LoginRequest;
import com.zalmuk.swwim.api.dto.auth.LoginResponse;
import com.zalmuk.swwim.api.dto.auth.RegisterRequest;
import com.zalmuk.swwim.api.dto.auth.TokenResponse;
import com.zalmuk.swwim.api.dto.user.UserResponse;
import com.zalmuk.swwim.api.entity.enums.AuthProvider;
import com.zalmuk.swwim.api.entity.user.RefreshToken;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.entity.user.UserSettings;
import com.zalmuk.swwim.api.entity.user.UserStats;
import com.zalmuk.swwim.api.repository.user.RefreshTokenRepository;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import com.zalmuk.swwim.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 인증 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthService oAuthService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 소셜 로그인 처리
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1. OAuth 토큰 검증 및 사용자 정보 조회
        OAuthUserInfo userInfo = oAuthService.verifyToken(request.getProvider(), request.getIdToken());

        // 2. 사용자 조회 또는 생성
        User user = userRepository.findById(userInfo.getId())
                .orElseGet(() -> createUser(userInfo));

        // 3. 마지막 로그인 시간 업데이트
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // 4. JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // 5. Refresh Token 저장
        saveRefreshToken(user, refreshToken, request.getDeviceInfo());

        // 6. 응답 생성
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtTokenProvider.getRefreshTokenExpiration() / 1000); // 초 단위
        response.setUser(UserResponse.from(user));
        response.setIsNewUser(user.getNickname() == null); // 닉네임이 없으면 신규 사용자

        return response;
    }

    /**
     * 이메일 회원가입
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // 1. 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 새 사용자 생성
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, request.getEmail(), AuthProvider.EMAIL);
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 기본 설정 생성
        UserSettings settings = new UserSettings(user);
        user.setSettings(settings);

        // 기본 통계 생성
        UserStats stats = new UserStats(user);
        user.setStats(stats);

        user = userRepository.save(user);

        // 3. JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // 4. Refresh Token 저장
        saveRefreshToken(user, refreshToken, null);

        // 5. 응답 생성
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtTokenProvider.getRefreshTokenExpiration() / 1000);
        response.setUser(UserResponse.from(user));
        response.setIsNewUser(true);

        return response;
    }

    /**
     * 이메일 로그인
     */
    @Transactional
    public LoginResponse loginWithEmail(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken();

        saveRefreshToken(user, refreshToken, null);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtTokenProvider.getRefreshTokenExpiration() / 1000);
        response.setUser(UserResponse.from(user));
        response.setIsNewUser(false);

        return response;
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public TokenResponse refreshToken(String refreshTokenValue) {
        // 1. Refresh Token 검증
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndIsRevokedFalse(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

        if (refreshToken.isExpired()) {
            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다. 다시 로그인해주세요.");
        }

        User user = refreshToken.getUser();

        // 2. 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(user);

        // 3. 응답 생성
        TokenResponse response = new TokenResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(refreshTokenValue); // 기존 refresh token 유지
        response.setExpiresIn(jwtTokenProvider.getRefreshTokenExpiration() / 1000);

        return response;
    }

    /**
     * 비밀번호 변경 (이메일 가입 사용자만)
     */
    @Transactional
    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getPassword() == null) {
            throw new IllegalArgumentException("소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 로그아웃 - 현재 기기의 Refresh Token 무효화
     */
    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token);
                });
    }

    /**
     * 전체 로그아웃 - 모든 기기에서 로그아웃
     */
    @Transactional
    public void logoutAll(String userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    /**
     * 새 사용자 생성
     */
    private User createUser(OAuthUserInfo userInfo) {
        User user = new User(userInfo.getId(), userInfo.getEmail(), userInfo.getProvider());
        user.setName(userInfo.getName());
        user.setProfileImageUrl(userInfo.getProfileImageUrl());
        user.setProviderId(userInfo.getProviderId());

        // 기본 설정 생성
        UserSettings settings = new UserSettings(user);
        user.setSettings(settings);

        // 기본 통계 생성
        UserStats stats = new UserStats(user);
        user.setStats(stats);

        return userRepository.save(user);
    }

    /**
     * Refresh Token 저장
     */
    private void saveRefreshToken(User user, String token, String deviceInfo) {
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtTokenProvider.getRefreshTokenExpiration() / 1000);

        RefreshToken refreshToken = new RefreshToken(user, token, expiresAt);
        refreshToken.setDeviceInfo(deviceInfo);

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * 만료된 토큰 정리 (스케줄러에서 호출)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}

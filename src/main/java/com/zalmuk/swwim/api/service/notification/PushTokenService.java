package com.zalmuk.swwim.api.service.notification;

import com.zalmuk.swwim.api.dto.notification.PushTokenRequest;
import com.zalmuk.swwim.api.entity.notification.UserPushToken;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.repository.notification.UserPushTokenRepository;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PushTokenService {

    private final UserPushTokenRepository pushTokenRepository;
    private final UserRepository userRepository;

    public PushTokenService(UserPushTokenRepository pushTokenRepository, UserRepository userRepository) {
        this.pushTokenRepository = pushTokenRepository;
        this.userRepository = userRepository;
    }

    public List<UserPushToken> getTokens(String userId) {
        return pushTokenRepository.findByUserIdOrderByLastUsedAtDesc(userId);
    }

    @Transactional
    public UserPushToken upsert(String userId, PushTokenRequest request) {
        validatePlatformProvider(request.getPlatform(), request.getProvider());
        String tokenValue = request.getToken().trim();
        if (tokenValue.isEmpty()) {
            throw new IllegalArgumentException("푸시 토큰은 필수입니다.");
        }

        return pushTokenRepository.findByUserIdAndToken(userId, tokenValue)
                .map(existing -> {
                    existing.update(
                            normalize(request.getPlatform()),
                            normalize(request.getProvider()),
                            blankToNull(request.getDeviceId()),
                            blankToNull(request.getAppVersion())
                    );
                    return pushTokenRepository.save(existing);
                })
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
                    UserPushToken pushToken = new UserPushToken(
                            user,
                            normalize(request.getPlatform()),
                            normalize(request.getProvider()),
                            tokenValue
                    );
                    pushToken.setDeviceId(blankToNull(request.getDeviceId()));
                    pushToken.setAppVersion(blankToNull(request.getAppVersion()));
                    return pushTokenRepository.save(pushToken);
                });
    }

    @Transactional
    public void deleteById(String userId, UUID id) {
        UserPushToken token = pushTokenRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("푸시 토큰을 찾을 수 없습니다."));
        pushTokenRepository.delete(token);
    }

    @Transactional
    public int deleteByDevice(String userId, String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("device 값은 필수입니다.");
        }
        List<UserPushToken> tokens = pushTokenRepository.findByUserIdAndDeviceId(userId, deviceId);
        pushTokenRepository.deleteAll(tokens);
        return tokens.size();
    }

    private void validatePlatformProvider(String platform, String provider) {
        String normalizedPlatform = normalize(platform);
        String normalizedProvider = normalize(provider);
        boolean validPlatform = "ios".equals(normalizedPlatform) || "android".equals(normalizedPlatform);
        boolean validProvider = "apns".equals(normalizedProvider) || "fcm".equals(normalizedProvider);
        if (!validPlatform || !validProvider) {
            throw new IllegalArgumentException("지원하지 않는 푸시 토큰 형식입니다.");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

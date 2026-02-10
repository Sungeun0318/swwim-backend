package com.zalmuk.swwim.api.service.user;

import com.zalmuk.swwim.api.entity.enums.AuthProvider;
import com.zalmuk.swwim.api.entity.enums.UserStatus;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.entity.user.UserSettings;
import com.zalmuk.swwim.api.entity.user.UserStats;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import com.zalmuk.swwim.api.repository.user.UserSettingsRepository;
import com.zalmuk.swwim.api.repository.user.UserStatsRepository;
import com.zalmuk.swwim.api.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final UserStatsRepository userStatsRepository;
    private final S3Service s3Service;

    public UserService(UserRepository userRepository,
                       UserSettingsRepository userSettingsRepository,
                       UserStatsRepository userStatsRepository,
                       S3Service s3Service) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.userStatsRepository = userStatsRepository;
        this.s3Service = s3Service;
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.searchByNameOrNickname(keyword);
    }

    @Transactional
    public User createUser(String id, String email, AuthProvider provider, String providerId) {
        User user = new User(id, email, provider);
        user.setProviderId(providerId);
        user = userRepository.save(user);

        // 기본 설정 생성
        UserSettings settings = new UserSettings(user);
        userSettingsRepository.save(settings);

        // 기본 통계 생성
        UserStats stats = new UserStats(user);
        userStatsRepository.save(stats);

        return user;
    }

    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void updateLastLogin(String userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    @Transactional
    public void updateUserStatus(String userId, UserStatus status) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(status);
            userRepository.save(user);
        });
    }

    @Transactional
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public Optional<UserSettings> getSettings(String userId) {
        return userSettingsRepository.findById(userId);
    }

    @Transactional
    public UserSettings updateSettings(UserSettings settings) {
        return userSettingsRepository.save(settings);
    }

    public Optional<UserStats> getStats(String userId) {
        return userStatsRepository.findById(userId);
    }

    @Transactional
    public UserStats updateStats(UserStats stats) {
        return userStatsRepository.save(stats);
    }

    /**
     * 프로필 이미지 업로드 (S3)
     */
    @Transactional
    public String uploadProfileImage(String userId, MultipartFile file) {
        // 파일 검증
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }

        // S3에 업로드
        String imageUrl = s3Service.uploadProfileImage(file, userId);

        // 사용자 프로필 이미지 URL 업데이트
        userRepository.findById(userId).ifPresent(user -> {
            // 기존 이미지 삭제
            if (user.getProfileImageUrl() != null) {
                s3Service.deleteFile(user.getProfileImageUrl());
            }
            user.setProfileImageUrl(imageUrl);
            userRepository.save(user);
        });

        return imageUrl;
    }
}

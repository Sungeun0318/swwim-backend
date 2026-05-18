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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final UserStatsRepository userStatsRepository;
    private final S3Service s3Service;

    @PersistenceContext
    private EntityManager em;

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

    /**
     * 즉시 회원 탈퇴: 사용자와 연관된 모든 데이터를 영구 삭제한다.
     * - 본인 활동(좋아요/댓글/저장) → 본인 게시글 및 그 하위 → 훈련/캘린더/채팅/퀵스타트 → 기타 단일 참조 → 양방향(M:N) → 1:1 → users
     * - S3 프로필 이미지는 best-effort 로 정리한다.
     */
    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        // S3 프로필 이미지 정리 (실패해도 진행)
        try {
            String img = user.getProfileImageUrl();
            if (img != null && !img.isEmpty()) {
                s3Service.deleteFile(img);
            }
        } catch (Exception e) {
            log.warn("프로필 이미지 S3 삭제 실패 userId={}: {}", userId, e.getMessage());
        }

        // (a) 본인이 다른 컨텐츠에 남긴 활동
        exec("DELETE FROM saved_posts WHERE user_id = :uid", userId);
        exec("DELETE FROM post_likes WHERE user_id = :uid", userId);
        exec("DELETE FROM comment_likes WHERE user_id = :uid", userId);
        exec("DELETE FROM post_comments WHERE user_id = :uid", userId);

        // (b) 본인 게시글 + 그 게시글에 달린 모든 좋아요/댓글/저장
        exec("DELETE FROM saved_posts WHERE post_id IN (SELECT id FROM community_posts WHERE user_id = :uid)", userId);
        exec("DELETE FROM post_likes WHERE post_id IN (SELECT id FROM community_posts WHERE user_id = :uid)", userId);
        exec("DELETE FROM comment_likes WHERE comment_id IN (SELECT id FROM post_comments WHERE post_id IN (SELECT id FROM community_posts WHERE user_id = :uid))", userId);
        exec("DELETE FROM post_comments WHERE post_id IN (SELECT id FROM community_posts WHERE user_id = :uid)", userId);
        exec("DELETE FROM community_posts WHERE user_id = :uid", userId);

        // (c) 훈련 데이터
        exec("DELETE FROM training_result_details WHERE result_id IN (SELECT id FROM training_results WHERE user_id = :uid)", userId);
        exec("DELETE FROM training_results WHERE user_id = :uid", userId);
        exec("DELETE FROM training_details WHERE session_id IN (SELECT id FROM training_sessions WHERE user_id = :uid)", userId);
        exec("DELETE FROM training_sessions WHERE user_id = :uid", userId);
        exec("DELETE FROM training_templates WHERE user_id = :uid", userId);
        exec("DELETE FROM training_backups WHERE user_id = :uid", userId);

        // (d) 채팅 (본인이 참여한 방의 모든 메시지 + 방 자체 삭제)
        exec("DELETE FROM chat_messages WHERE sender_id = :uid OR room_id IN (SELECT id FROM chat_rooms WHERE participant1_id = :uid OR participant2_id = :uid)", userId);
        exec("DELETE FROM chat_rooms WHERE participant1_id = :uid OR participant2_id = :uid", userId);

        // (e) 퀵스타트
        exec("DELETE FROM quickstart_daily_completions WHERE week_progress_id IN (SELECT id FROM quickstart_week_progress WHERE progress_id IN (SELECT id FROM quickstart_progress WHERE user_id = :uid))", userId);
        exec("DELETE FROM quickstart_week_progress WHERE progress_id IN (SELECT id FROM quickstart_progress WHERE user_id = :uid)", userId);
        exec("DELETE FROM quickstart_progress WHERE user_id = :uid", userId);
        exec("DELETE FROM user_achievements WHERE user_id = :uid", userId);

        // (f) 기타 직접 참조
        exec("DELETE FROM calendar_events WHERE user_id = :uid", userId);
        exec("DELETE FROM pool_reviews WHERE user_id = :uid", userId);
        exec("DELETE FROM saved_pools WHERE user_id = :uid", userId);
        exec("DELETE FROM notifications WHERE user_id = :uid", userId);
        exec("DELETE FROM user_push_tokens WHERE user_id = :uid", userId);
        exec("DELETE FROM user_entitlements WHERE user_id = :uid", userId);
        exec("DELETE FROM deletion_requests WHERE user_id = :uid", userId);
        exec("DELETE FROM refresh_tokens WHERE user_id = :uid", userId);
        exec("DELETE FROM reports WHERE reporter_id = :uid", userId);
        exec("UPDATE reports SET resolved_by = NULL WHERE resolved_by = :uid", userId);
        exec("DELETE FROM user_sanctions WHERE user_id = :uid OR admin_id = :uid", userId);

        // (g) 양방향 관계
        exec("DELETE FROM user_follows WHERE follower_id = :uid OR following_id = :uid", userId);
        exec("DELETE FROM blocked_users WHERE user_id = :uid OR blocked_user_id = :uid", userId);

        // (h) 1:1 (User 엔티티의 cascade 회피하기 위해 명시적으로 먼저 삭제)
        exec("DELETE FROM user_settings WHERE user_id = :uid", userId);
        exec("DELETE FROM user_stats WHERE user_id = :uid", userId);

        // 영속성 컨텍스트 정리 후 native delete (JPA cascade 재발동 방지)
        em.flush();
        em.clear();

        exec("DELETE FROM users WHERE id = :uid", userId);

        log.info("회원 탈퇴 완료 (즉시 삭제) userId={}", userId);
    }

    private void exec(String sql, String userId) {
        em.createNativeQuery(sql).setParameter("uid", userId).executeUpdate();
    }

    @Transactional
    public void updateFcmToken(String userId, String fcmToken) {
        userSettingsRepository.findById(userId).ifPresent(settings -> {
            settings.setFcmToken(fcmToken);
            userSettingsRepository.save(settings);
        });
    }

    @Transactional
    public void updateSelectedPool(String userId, String poolId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setSelectedPoolId(poolId);
            userRepository.save(user);
        });
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

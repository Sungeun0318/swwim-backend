package com.zalmuk.swwim.api.service.notification;

import com.zalmuk.swwim.api.dto.notification.NotificationResponse;
import com.zalmuk.swwim.api.entity.enums.NotificationType;
import com.zalmuk.swwim.api.entity.notification.Notification;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.repository.notification.NotificationRepository;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Optional<Notification> findById(UUID id) {
        return notificationRepository.findById(id);
    }

    public Page<Notification> getUserNotifications(String userId, Pageable pageable) {
        return userRepository.findById(userId)
                .map(user -> notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable))
                .orElse(Page.empty());
    }

    public Page<Notification> getUnreadNotifications(String userId, Pageable pageable) {
        return notificationRepository.findUnreadByUserId(userId, pageable);
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public Notification createNotification(String userId, NotificationType type,
                                            String title, String body,
                                            String relatedId, String actionUrl) {
        return createNotification(userId, type, title, body, relatedId, actionUrl, null);
    }

    @Transactional
    public Notification createNotification(String userId, NotificationType type,
                                            String title, String body,
                                            String relatedId, String actionUrl,
                                            String senderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Notification notification = new Notification(user, type, title, body);
        notification.setRelatedId(relatedId);
        notification.setActionUrl(actionUrl);
        notification.setSenderId(senderId);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification createScheduledNotification(String userId, NotificationType type,
                                                     String title, String body,
                                                     LocalDateTime scheduledFor) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Notification notification = new Notification(user, type, title, body);
        notification.setScheduledFor(scheduledFor);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.markAsRead();
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Transactional
    public void deleteNotification(UUID notificationId, String userId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (!notification.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("본인의 알림만 삭제할 수 있습니다.");
            }
            notificationRepository.delete(notification);
        });
    }

    public List<Notification> getPendingScheduledNotifications() {
        return notificationRepository.findPendingScheduledNotifications(LocalDateTime.now());
    }

    @Transactional
    public void markAsSent(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.markAsSent();
            notificationRepository.save(notification);
        });
    }

    /**
     * Notification을 NotificationResponse로 변환하면서 발신자 프로필 정보를 채운다.
     */
    public NotificationResponse toResponseWithSenderInfo(Notification notification) {
        NotificationResponse response = NotificationResponse.from(notification);

        String senderId = notification.getSenderId();
        if (senderId != null) {
            userRepository.findById(senderId).ifPresent(sender -> {
                response.setSenderProfileImageUrl(sender.getProfileImageUrl());
                response.setSenderNickname(sender.getNickname());
            });
        }

        return response;
    }

    // 알림 생성 헬퍼 메서드 - REQUIRES_NEW: 호출자 트랜잭션과 분리, 알림 실패가 본 기능에 영향 없도록
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendFollowNotification(String followerId, String followingId) {
        userRepository.findById(followerId).ifPresent(follower -> {
            String title = "새로운 팔로워";
            String body = follower.getNickname() + "님이 회원님을 팔로우했습니다.";
            createNotification(followingId, NotificationType.FOLLOW, title, body, followerId, null, followerId);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendLikeNotification(String likerId, String postOwnerId, String postId) {
        if (likerId.equals(postOwnerId)) return; // 본인 게시글 좋아요는 알림 안함

        userRepository.findById(likerId).ifPresent(liker -> {
            String title = "좋아요";
            String body = liker.getNickname() + "님이 게시글을 좋아합니다.";
            createNotification(postOwnerId, NotificationType.LIKE, title, body, postId, null, likerId);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendCommentNotification(String commenterId, String postOwnerId, String postId) {
        if (commenterId.equals(postOwnerId)) return;

        userRepository.findById(commenterId).ifPresent(commenter -> {
            String title = "새 댓글";
            String body = commenter.getNickname() + "님이 댓글을 남겼습니다.";
            createNotification(postOwnerId, NotificationType.COMMENT, title, body, postId, null, commenterId);
        });
    }
}

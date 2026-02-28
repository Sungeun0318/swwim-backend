package com.zalmuk.swwim.api.service.user;

import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.entity.user.UserFollow;
import com.zalmuk.swwim.api.repository.user.UserFollowRepository;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import com.zalmuk.swwim.api.service.notification.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FollowService {

    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public FollowService(UserFollowRepository userFollowRepository, UserRepository userRepository,
                         NotificationService notificationService) {
        this.userFollowRepository = userFollowRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public boolean isFollowing(String followerId, String followingId) {
        return userRepository.findById(followerId)
                .flatMap(follower -> userRepository.findById(followingId)
                        .map(following -> userFollowRepository.existsByFollowerAndFollowing(follower, following)))
                .orElse(false);
    }

    @Transactional
    public void follow(String followerId, String followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("팔로워를 찾을 수 없습니다."));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 대상을 찾을 수 없습니다."));

        if (userFollowRepository.existsByFollowerAndFollowing(follower, following)) {
            return; // 이미 팔로우 중
        }

        UserFollow userFollow = new UserFollow(follower, following);
        userFollowRepository.save(userFollow);

        // 카운터 업데이트
        follower.setFollowingCount(follower.getFollowingCount() + 1);
        following.setFollowersCount(following.getFollowersCount() + 1);
        userRepository.save(follower);
        userRepository.save(following);

        // 팔로우 알림 생성
        notificationService.sendFollowNotification(followerId, followingId);
    }

    @Transactional
    public void unfollow(String followerId, String followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("팔로워를 찾을 수 없습니다."));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("언팔로우 대상을 찾을 수 없습니다."));

        userFollowRepository.findByFollowerAndFollowing(follower, following).ifPresent(userFollow -> {
            userFollowRepository.delete(userFollow);

            // 카운터 업데이트
            follower.setFollowingCount(Math.max(0, follower.getFollowingCount() - 1));
            following.setFollowersCount(Math.max(0, following.getFollowersCount() - 1));
            userRepository.save(follower);
            userRepository.save(following);
        });
    }

    public Page<User> getFollowing(String userId, Pageable pageable) {
        return userFollowRepository.findFollowingByUserId(userId, pageable);
    }

    public Page<User> getFollowers(String userId, Pageable pageable) {
        return userFollowRepository.findFollowersByUserId(userId, pageable);
    }

    public long getFollowingCount(String userId) {
        return userFollowRepository.countByFollowerId(userId);
    }

    public long getFollowersCount(String userId) {
        return userFollowRepository.countByFollowingId(userId);
    }
}

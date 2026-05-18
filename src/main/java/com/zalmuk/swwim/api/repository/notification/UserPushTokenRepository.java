package com.zalmuk.swwim.api.repository.notification;

import com.zalmuk.swwim.api.entity.notification.UserPushToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPushTokenRepository extends JpaRepository<UserPushToken, UUID> {

    List<UserPushToken> findByUserIdOrderByLastUsedAtDesc(String userId);

    Optional<UserPushToken> findByUserIdAndToken(String userId, String token);

    Optional<UserPushToken> findByIdAndUserId(UUID id, String userId);

    List<UserPushToken> findByUserIdAndDeviceId(String userId, String deviceId);
}

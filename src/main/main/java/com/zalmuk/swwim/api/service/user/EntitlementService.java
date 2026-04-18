package com.zalmuk.swwim.api.service.user;

import com.zalmuk.swwim.api.entity.enums.EntitlementSource;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.entity.user.UserEntitlement;
import com.zalmuk.swwim.api.repository.user.UserEntitlementRepository;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entitlement 관리 서비스 (기능 권한 레이어)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntitlementService {

    private final UserEntitlementRepository entitlementRepository;
    private final UserRepository userRepository;

    // 프리미엄 entitlement 상수
    public static final String PREMIUM = "premium";

    /**
     * 특정 entitlement 보유 여부 확인
     */
    public boolean hasEntitlement(String userId, String entitlement) {
        // ADMIN은 모든 entitlement 보유
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && Boolean.TRUE.equals(user.getIsAdmin())) {
            return true;
        }
        return entitlementRepository.hasActiveEntitlement(userId, entitlement, LocalDateTime.now());
    }

    /**
     * 프리미엄 여부 확인
     */
    public boolean isPremium(String userId) {
        return hasEntitlement(userId, PREMIUM);
    }

    /**
     * 활성 entitlement 목록 조회
     */
    public List<UserEntitlement> getActiveEntitlements(String userId) {
        return entitlementRepository.findActiveByUserId(userId, LocalDateTime.now());
    }

    /**
     * 모든 entitlement 조회 (만료 포함)
     */
    public List<UserEntitlement> getAllEntitlements(String userId) {
        return entitlementRepository.findByUserId(userId);
    }

    /**
     * 관리자가 수동으로 entitlement 부여
     */
    @Transactional
    public UserEntitlement grantEntitlement(String userId, String entitlement,
                                             String grantedBy, LocalDateTime expiresAt) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이미 해당 entitlement가 있으면 업데이트
        UserEntitlement existing = entitlementRepository
                .findByUserIdAndEntitlement(userId, entitlement)
                .orElse(null);

        if (existing != null) {
            existing.setSource(EntitlementSource.ADMIN_GRANT);
            existing.setGrantedBy(grantedBy);
            existing.setExpiresAt(expiresAt);
            return entitlementRepository.save(existing);
        }

        // 새로 생성
        UserEntitlement ent = new UserEntitlement(user, entitlement, EntitlementSource.ADMIN_GRANT);
        ent.setGrantedBy(grantedBy);
        ent.setExpiresAt(expiresAt);

        // User의 isPremium 필드도 동기화
        if (PREMIUM.equals(entitlement)) {
            user.setIsPremium(true);
            user.setPremiumStartedAt(LocalDateTime.now());
            user.setPremiumExpiresAt(expiresAt);
            userRepository.save(user);
        }

        return entitlementRepository.save(ent);
    }

    /**
     * RevenueCat 웹훅으로 entitlement 부여
     */
    @Transactional
    public UserEntitlement grantFromRevenueCat(String userId, String entitlement, LocalDateTime expiresAt) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserEntitlement existing = entitlementRepository
                .findByUserIdAndEntitlement(userId, entitlement)
                .orElse(null);

        if (existing != null) {
            existing.setSource(EntitlementSource.REVENUECAT);
            existing.setExpiresAt(expiresAt);
            existing.setGrantedBy(null);
            return entitlementRepository.save(existing);
        }

        UserEntitlement ent = new UserEntitlement(user, entitlement, EntitlementSource.REVENUECAT);
        ent.setExpiresAt(expiresAt);

        // User의 isPremium 필드도 동기화
        if (PREMIUM.equals(entitlement)) {
            user.setIsPremium(true);
            user.setPremiumStartedAt(LocalDateTime.now());
            user.setPremiumExpiresAt(expiresAt);
            userRepository.save(user);
        }

        return entitlementRepository.save(ent);
    }

    /**
     * Entitlement 취소/삭제
     */
    @Transactional
    public void revokeEntitlement(String userId, String entitlement) {
        entitlementRepository.deleteByUserIdAndEntitlement(userId, entitlement);

        // User의 isPremium 필드도 동기화
        if (PREMIUM.equals(entitlement)) {
            userRepository.findById(userId).ifPresent(user -> {
                user.setIsPremium(false);
                user.setPremiumExpiresAt(null);
                userRepository.save(user);
            });
        }
    }

    /**
     * 만료된 entitlement 정리 (스케줄러에서 호출)
     */
    @Transactional
    public void cleanupExpired() {
        entitlementRepository.deleteExpiredEntitlements(LocalDateTime.now());
    }
}

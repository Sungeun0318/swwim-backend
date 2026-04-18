package com.zalmuk.swwim.api.repository.user;

import com.zalmuk.swwim.api.entity.user.UserEntitlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEntitlementRepository extends JpaRepository<UserEntitlement, UUID> {

    // 특정 유저의 모든 entitlement 조회
    List<UserEntitlement> findByUserId(String userId);

    // 특정 유저의 특정 entitlement 조회
    Optional<UserEntitlement> findByUserIdAndEntitlement(String userId, String entitlement);

    // 특정 유저가 특정 entitlement을 갖고 있는지 (만료 안 된 것만)
    @Query("SELECT COUNT(e) > 0 FROM UserEntitlement e " +
            "WHERE e.user.id = :userId AND e.entitlement = :entitlement " +
            "AND (e.expiresAt IS NULL OR e.expiresAt > :now)")
    boolean hasActiveEntitlement(@Param("userId") String userId,
                                  @Param("entitlement") String entitlement,
                                  @Param("now") LocalDateTime now);

    // 특정 유저의 활성 entitlement 목록
    @Query("SELECT e FROM UserEntitlement e " +
            "WHERE e.user.id = :userId " +
            "AND (e.expiresAt IS NULL OR e.expiresAt > :now)")
    List<UserEntitlement> findActiveByUserId(@Param("userId") String userId,
                                              @Param("now") LocalDateTime now);

    // 특정 유저의 특정 entitlement 삭제
    void deleteByUserIdAndEntitlement(String userId, String entitlement);

    // 만료된 entitlement 정리
    @Modifying
    @Query("DELETE FROM UserEntitlement e WHERE e.expiresAt IS NOT NULL AND e.expiresAt < :now")
    void deleteExpiredEntitlements(@Param("now") LocalDateTime now);
}

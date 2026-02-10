package com.zalmuk.swwim.api.repository.user;

import com.zalmuk.swwim.api.entity.user.RefreshToken;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenAndIsRevokedFalse(String token);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.isRevoked = true WHERE r.user = :user")
    void revokeAllByUser(@Param("user") User user);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.isRevoked = true WHERE r.user.id = :userId")
    void revokeAllByUserId(@Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    long countByUserAndIsRevokedFalse(User user);
}

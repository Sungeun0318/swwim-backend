package com.zalmuk.swwim.api.repository.admin;

import com.zalmuk.swwim.api.entity.admin.UserSanction;
import com.zalmuk.swwim.api.entity.enums.SanctionType;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSanctionRepository extends JpaRepository<UserSanction, UUID> {

    Page<UserSanction> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Query("SELECT us FROM UserSanction us WHERE us.user.id = :userId AND us.isActive = true")
    List<UserSanction> findActiveByUserId(@Param("userId") String userId);

    @Query("SELECT us FROM UserSanction us WHERE us.user.id = :userId AND us.isActive = true AND us.sanctionType = :type")
    Optional<UserSanction> findActiveByUserIdAndType(@Param("userId") String userId, @Param("type") SanctionType type);

    @Query("SELECT us FROM UserSanction us WHERE us.isActive = true ORDER BY us.createdAt DESC")
    Page<UserSanction> findAllActive(Pageable pageable);

    @Query("SELECT us FROM UserSanction us WHERE us.isActive = true AND us.endDate IS NOT NULL AND us.endDate < CURRENT_TIMESTAMP")
    List<UserSanction> findExpiredSanctions();

    boolean existsByUserIdAndIsActiveTrue(String userId);
}

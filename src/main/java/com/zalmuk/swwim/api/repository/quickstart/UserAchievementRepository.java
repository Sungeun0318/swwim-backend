package com.zalmuk.swwim.api.repository.quickstart;

import com.zalmuk.swwim.api.entity.quickstart.UserAchievement;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {

    Page<UserAchievement> findByUserOrderByEarnedAtDesc(User user, Pageable pageable);

    List<UserAchievement> findByUserAndAchievementType(User user, String achievementType);

    Optional<UserAchievement> findByUserAndAchievementTypeAndAchievementName(User user, String achievementType, String achievementName);

    boolean existsByUserAndAchievementTypeAndAchievementName(User user, String achievementType, String achievementName);

    long countByUserId(String userId);
}

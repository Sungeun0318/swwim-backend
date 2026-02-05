package com.zalmuk.swwim.api.repository.user;

import com.zalmuk.swwim.api.entity.user.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, String> {

    @Query("SELECT us FROM UserStats us ORDER BY us.totalDistance DESC")
    List<UserStats> findTopByTotalDistance();

    @Query("SELECT us FROM UserStats us ORDER BY us.longestStreak DESC")
    List<UserStats> findTopByLongestStreak();

    @Query("SELECT us FROM UserStats us WHERE us.currentStreak > 0 ORDER BY us.currentStreak DESC")
    List<UserStats> findUsersWithActiveStreak();
}

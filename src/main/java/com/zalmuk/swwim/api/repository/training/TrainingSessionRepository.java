package com.zalmuk.swwim.api.repository.training;

import com.zalmuk.swwim.api.entity.enums.TrainingStatus;
import com.zalmuk.swwim.api.entity.training.TrainingSession;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, UUID> {

    Page<TrainingSession> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<TrainingSession> findByUserAndStatus(User user, TrainingStatus status);

    @Query("SELECT ts FROM TrainingSession ts WHERE ts.user.id = :userId AND ts.isCompleted = true ORDER BY ts.completedAt DESC")
    Page<TrainingSession> findCompletedByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT ts FROM TrainingSession ts WHERE ts.user.id = :userId AND ts.createdAt BETWEEN :startDate AND :endDate")
    List<TrainingSession> findByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(ts.totalDistance) FROM TrainingSession ts WHERE ts.user.id = :userId AND ts.isCompleted = true")
    Long sumTotalDistanceByUserId(@Param("userId") String userId);

    @Query("SELECT SUM(ts.totalTime) FROM TrainingSession ts WHERE ts.user.id = :userId AND ts.isCompleted = true")
    Long sumTotalTimeByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(ts) FROM TrainingSession ts WHERE ts.user.id = :userId AND ts.isCompleted = true")
    long countCompletedByUserId(@Param("userId") String userId);
}

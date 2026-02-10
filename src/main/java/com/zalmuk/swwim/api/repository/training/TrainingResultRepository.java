package com.zalmuk.swwim.api.repository.training;

import com.zalmuk.swwim.api.entity.training.TrainingResult;
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
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainingResultRepository extends JpaRepository<TrainingResult, UUID> {

    @Query("SELECT tr FROM TrainingResult tr JOIN FETCH tr.user JOIN FETCH tr.session WHERE tr.user = :user ORDER BY tr.completedAt DESC")
    Page<TrainingResult> findByUserOrderByCompletedAtDesc(@Param("user") User user, Pageable pageable);

    @Query("SELECT tr FROM TrainingResult tr JOIN FETCH tr.user JOIN FETCH tr.session WHERE tr.session = :session")
    Optional<TrainingResult> findBySession(@Param("session") TrainingSession session);

    @Query("SELECT tr FROM TrainingResult tr JOIN FETCH tr.user JOIN FETCH tr.session WHERE tr.user.id = :userId AND tr.completedAt BETWEEN :startDate AND :endDate ORDER BY tr.completedAt DESC")
    List<TrainingResult> findByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT tr FROM TrainingResult tr JOIN FETCH tr.user JOIN FETCH tr.session WHERE tr.user.id = :userId AND tr.addedToCalendar = false")
    List<TrainingResult> findNotAddedToCalendar(@Param("userId") String userId);

    long countByUserId(String userId);
}

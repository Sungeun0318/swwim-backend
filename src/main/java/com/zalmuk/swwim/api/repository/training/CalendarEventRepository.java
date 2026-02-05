package com.zalmuk.swwim.api.repository.training;

import com.zalmuk.swwim.api.entity.training.CalendarEvent;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, UUID> {

    Optional<CalendarEvent> findByUserAndDate(User user, LocalDate date);

    @Query("SELECT ce FROM CalendarEvent ce WHERE ce.user.id = :userId AND ce.date BETWEEN :startDate AND :endDate ORDER BY ce.date ASC")
    List<CalendarEvent> findByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT ce FROM CalendarEvent ce WHERE ce.scheduledDateTime IS NOT NULL AND ce.scheduledDateTime BETWEEN :start AND :end")
    List<CalendarEvent> findScheduledEvents(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT ce.date FROM CalendarEvent ce WHERE ce.user.id = :userId AND ce.date BETWEEN :startDate AND :endDate")
    List<LocalDate> findDatesWithEventsByUserIdAndRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    boolean existsByUserAndDate(User user, LocalDate date);

    void deleteByUserAndDate(User user, LocalDate date);
}

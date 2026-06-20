package com.zalmuk.swwim.api.repository.training;

import com.zalmuk.swwim.api.entity.training.CalendarEvent;
import com.zalmuk.swwim.api.entity.training.WatchWorkout;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WatchWorkoutRepository extends JpaRepository<WatchWorkout, UUID> {

    @Query("SELECT ww FROM WatchWorkout ww JOIN FETCH ww.user LEFT JOIN FETCH ww.calendarEvent WHERE ww.user = :user AND ww.externalId = :externalId")
    Optional<WatchWorkout> findByUserAndExternalId(@Param("user") User user, @Param("externalId") String externalId);

    @Query("SELECT ww FROM WatchWorkout ww JOIN FETCH ww.user LEFT JOIN FETCH ww.calendarEvent WHERE ww.calendarEvent = :calendarEvent ORDER BY ww.updatedAt DESC")
    List<WatchWorkout> findByCalendarEvent(@Param("calendarEvent") CalendarEvent calendarEvent);

    @Query("SELECT ww FROM WatchWorkout ww JOIN FETCH ww.user LEFT JOIN FETCH ww.calendarEvent WHERE ww.id = :id")
    Optional<WatchWorkout> findByIdWithUserAndCalendarEvent(@Param("id") UUID id);
}

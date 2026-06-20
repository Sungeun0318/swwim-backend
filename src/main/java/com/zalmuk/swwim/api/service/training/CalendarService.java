package com.zalmuk.swwim.api.service.training;

import com.zalmuk.swwim.api.entity.training.CalendarEvent;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.repository.training.CalendarEventRepository;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CalendarService {

    private final CalendarEventRepository calendarEventRepository;
    private final UserRepository userRepository;

    public CalendarService(CalendarEventRepository calendarEventRepository, UserRepository userRepository) {
        this.calendarEventRepository = calendarEventRepository;
        this.userRepository = userRepository;
    }

    public Optional<CalendarEvent> findById(UUID id) {
        return calendarEventRepository.findByIdWithUser(id);
    }

    public Optional<CalendarEvent> findByUserAndDate(String userId, LocalDate date) {
        return userRepository.findById(userId)
                .flatMap(user -> calendarEventRepository.findByUserAndDate(user, date));
    }

    public Optional<CalendarEvent> findByUserAndSessionId(User user, String sessionId) {
        return calendarEventRepository.findByUserAndSessionId(user, sessionId);
    }

    public List<CalendarEvent> findAllByUserAndDate(String userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return calendarEventRepository.findAllByUserAndDate(user, date);
    }

    public List<CalendarEvent> getEventsByDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        return calendarEventRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    public List<LocalDate> getDatesWithEvents(String userId, LocalDate startDate, LocalDate endDate) {
        return calendarEventRepository.findDatesWithEventsByUserIdAndRange(userId, startDate, endDate);
    }

    @Transactional
    public CalendarEvent createEvent(String userId, LocalDate date,
                                      String title, Integer totalDistance, String totalTime,
                                      List<Map<String, Object>> trainings,
                                      LocalDateTime scheduledDateTime,
                                      Boolean notify30minBefore, Boolean notify1hourBefore,
                                      String type, String memo) {
        return createEvent(userId, date, title, totalDistance, totalTime, trainings,
                scheduledDateTime, notify30minBefore, notify1hourBefore, type, memo, null);
    }

    @Transactional
    public CalendarEvent createEvent(String userId, LocalDate date,
                                      String title, Integer totalDistance, String totalTime,
                                      List<Map<String, Object>> trainings,
                                      LocalDateTime scheduledDateTime,
                                      Boolean notify30minBefore, Boolean notify1hourBefore,
                                      String type, String memo, String sessionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        CalendarEvent event = new CalendarEvent(user, date, trainings);
        if (title != null) event.setTitle(title);
        if (totalDistance != null) event.setTotalDistance(totalDistance);
        if (totalTime != null) event.setTotalTime(totalTime);
        if (scheduledDateTime != null) event.setScheduledDateTime(scheduledDateTime);
        if (notify30minBefore != null) event.setNotify30minBefore(notify30minBefore);
        if (notify1hourBefore != null) event.setNotify1hourBefore(notify1hourBefore);
        if (type != null) event.setType(type);
        if (memo != null) event.setMemo(memo);
        if (sessionId != null && !sessionId.isBlank()) event.setSessionId(sessionId);
        return calendarEventRepository.save(event);
    }

    @Transactional
    public CalendarEvent updateEvent(UUID eventId, String userId,
                                      String title, Integer totalDistance, String totalTime,
                                      List<Map<String, Object>> trainings) {
        CalendarEvent event = calendarEventRepository.findByIdWithUser(eventId)
                .orElseThrow(() -> new IllegalArgumentException("캘린더 이벤트를 찾을 수 없습니다."));

        if (!event.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 이벤트만 수정할 수 있습니다.");
        }

        if (title != null) event.setTitle(title);
        if (totalDistance != null) event.setTotalDistance(totalDistance);
        if (totalTime != null) event.setTotalTime(totalTime);
        if (trainings != null) event.setTrainings(trainings);
        return calendarEventRepository.save(event);
    }

    @Transactional
    public void completeEvent(UUID eventId, String userId) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("캘린더 이벤트를 찾을 수 없습니다."));
        event.setCompleted(true);
        calendarEventRepository.save(event);
    }

    @Transactional
    public void saveMemo(UUID eventId, String userId, String memo) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("캘린더 이벤트를 찾을 수 없습니다."));
        event.setMemo(memo);
        calendarEventRepository.save(event);
    }

    @Transactional
    public CalendarEvent updateEventNotification(UUID eventId, LocalDateTime scheduledDateTime,
                                                  boolean notify30min, boolean notify1hour) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("캘린더 이벤트를 찾을 수 없습니다."));

        event.setScheduledDateTime(scheduledDateTime);
        event.setNotify30minBefore(notify30min);
        event.setNotify1hourBefore(notify1hour);
        return calendarEventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(UUID eventId, String userId) {
        calendarEventRepository.findById(eventId).ifPresent(event -> {
            if (!event.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("본인의 이벤트만 삭제할 수 있습니다.");
            }
            calendarEventRepository.delete(event);
        });
    }

    @Transactional
    public void deleteEventByDate(String userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        calendarEventRepository.deleteByUserAndDate(user, date);
    }

    public List<CalendarEvent> getScheduledEvents(LocalDateTime start, LocalDateTime end) {
        return calendarEventRepository.findScheduledEvents(start, end);
    }

    public List<CalendarEvent> getAllUserEvents(String userId) {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2099, 12, 31);
        return calendarEventRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Transactional
    public CalendarEvent saveEvent(CalendarEvent event) {
        return calendarEventRepository.save(event);
    }
}

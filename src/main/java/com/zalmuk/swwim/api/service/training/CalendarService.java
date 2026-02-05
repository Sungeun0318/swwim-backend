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
        return calendarEventRepository.findById(id);
    }

    public Optional<CalendarEvent> findByUserAndDate(String userId, LocalDate date) {
        return userRepository.findById(userId)
                .flatMap(user -> calendarEventRepository.findByUserAndDate(user, date));
    }

    public List<CalendarEvent> getEventsByDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        return calendarEventRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    public List<LocalDate> getDatesWithEvents(String userId, LocalDate startDate, LocalDate endDate) {
        return calendarEventRepository.findDatesWithEventsByUserIdAndRange(userId, startDate, endDate);
    }

    @Transactional
    public CalendarEvent createOrUpdateEvent(String userId, LocalDate date, List<Map<String, Object>> trainings) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        CalendarEvent event = calendarEventRepository.findByUserAndDate(user, date)
                .orElse(new CalendarEvent(user, date, trainings));

        event.setTrainings(trainings);
        return calendarEventRepository.save(event);
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
}

package com.zalmuk.swwim.api.service.training;

import com.zalmuk.swwim.api.entity.enums.TrainingStatus;
import com.zalmuk.swwim.api.entity.training.*;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.entity.user.UserStats;
import com.zalmuk.swwim.api.repository.training.*;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import com.zalmuk.swwim.api.repository.user.UserStatsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TrainingService {

    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");

    private final TrainingSessionRepository sessionRepository;
    private final TrainingDetailRepository detailRepository;
    private final TrainingResultRepository resultRepository;
    private final TrainingResultDetailRepository resultDetailRepository;
    private final UserRepository userRepository;
    private final UserStatsRepository userStatsRepository;
    private final CalendarService calendarService;

    public TrainingService(TrainingSessionRepository sessionRepository,
                           TrainingDetailRepository detailRepository,
                           TrainingResultRepository resultRepository,
                           TrainingResultDetailRepository resultDetailRepository,
                           UserRepository userRepository,
                           UserStatsRepository userStatsRepository,
                           CalendarService calendarService) {
        this.sessionRepository = sessionRepository;
        this.detailRepository = detailRepository;
        this.resultRepository = resultRepository;
        this.resultDetailRepository = resultDetailRepository;
        this.userRepository = userRepository;
        this.userStatsRepository = userStatsRepository;
        this.calendarService = calendarService;
    }

    public Optional<TrainingSession> findSessionById(UUID id) {
        return sessionRepository.findByIdWithDetails(id);
    }

    public Page<TrainingSession> getUserSessions(String userId, Pageable pageable) {
        return userRepository.findById(userId)
                .map(user -> sessionRepository.findByUserOrderByCreatedAtDesc(user, pageable))
                .orElse(Page.empty());
    }

    public Page<TrainingSession> getCompletedSessions(String userId, Pageable pageable) {
        return sessionRepository.findCompletedByUserId(userId, pageable);
    }

    @Transactional
    public TrainingSession createSession(String userId, String title, String beepSound,
                                          Integer numPeople, Integer totalTime, Integer totalDistance) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        TrainingSession session = new TrainingSession(user, beepSound, totalTime, totalDistance);
        session.setTitle(title);
        session.setNumPeople(numPeople);
        return sessionRepository.save(session);
    }

    @Transactional
    public TrainingDetail addDetail(UUID sessionId, String title, Integer distance,
                                     Integer count, Integer cycle, Integer restTime,
                                     Integer interval, Integer personnel, Integer orderIndex) {
        TrainingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("훈련 세션을 찾을 수 없습니다."));

        TrainingDetail detail = new TrainingDetail(session, title, distance, count, cycle);
        detail.setRestTime(restTime != null ? restTime : 0);
        detail.setInterval(interval != null ? interval : 5);
        detail.setPersonnel(personnel != null ? personnel : 1);
        detail.setOrderIndex(orderIndex != null ? orderIndex : 0);
        return detailRepository.save(detail);
    }

    @Transactional
    public void updateSessionStatus(UUID sessionId, TrainingStatus status) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setStatus(status);
            if (status == TrainingStatus.COMPLETED) {
                session.setIsCompleted(true);
                session.setCompletedAt(LocalDateTime.now());
            }
            sessionRepository.save(session);
        });
    }

    @Transactional
    public TrainingResult completeSession(UUID sessionId, String totalTime, Integer totalDistance,
                                           List<TrainingResultDetail> details,
                                           Instant startedAt, Instant endedAt) {
        TrainingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("훈련 세션을 찾을 수 없습니다."));

        boolean firstCompletion = session.getStatus() != TrainingStatus.COMPLETED;
        Instant effectiveStartedAt = startedAt != null ? startedAt : session.getStartedAt();
        Instant effectiveEndedAt = endedAt != null ? endedAt : session.getEndedAt();
        if (effectiveEndedAt == null) effectiveEndedAt = Instant.now();

        // 세션 완료 처리
        session.setStatus(TrainingStatus.COMPLETED);
        session.setIsCompleted(true);
        session.setCompletedAt(LocalDateTime.now());
        session.setStartedAt(effectiveStartedAt);
        session.setEndedAt(effectiveEndedAt);
        sessionRepository.save(session);

        // 결과 저장: 같은 세션 재완료는 기존 결과를 갱신한다.
        TrainingResult result = resultRepository.findBySession(session)
                .orElseGet(() -> new TrainingResult(session.getUser(), session, totalTime, totalDistance));
        result.setTotalTime(totalTime);
        result.setTotalDistance(totalDistance);
        result.setCompletedAt(LocalDateTime.now());
        result = resultRepository.save(result);

        // 결과 상세 저장
        if (details != null) {
            for (TrainingResultDetail detail : details) {
                detail.setResult(result);
                resultDetailRepository.save(detail);
            }
        }

        // 사용자 통계 업데이트: 최초 완료에서만 집계한다.
        if (firstCompletion) {
            updateUserStats(session.getUser().getId(), totalDistance, parseTotalTimeToSeconds(totalTime));
        }

        // 캘린더 이벤트 자동 생성/갱신 (sessionId 기준 멱등)
        try {
            List<TrainingDetail> sessionDetails = detailRepository.findBySessionOrderByOrderIndexAsc(session);
            List<Map<String, Object>> calendarTrainings = sessionDetails.stream()
                    .map(d -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("title", d.getTitle());
                        m.put("distance", d.getDistance());
                        m.put("count", d.getCount());
                        m.put("cycle", d.getCycle());
                        return m;
                    })
                    .collect(Collectors.toList());

            String calendarSessionId = session.getId().toString();
            LocalDate eventDate = effectiveStartedAt != null
                    ? LocalDateTime.ofInstant(effectiveStartedAt, SERVICE_ZONE).toLocalDate()
                    : LocalDate.now(SERVICE_ZONE);
            CalendarEvent calendarEvent = calendarService.findByUserAndSessionId(session.getUser(), calendarSessionId)
                    .orElseGet(() -> {
                        CalendarEvent event = calendarService.createEvent(
                                session.getUser().getId(),
                                eventDate,
                                session.getTitle(),
                                totalDistance,
                                totalTime,
                                calendarTrainings,
                                null, null, null, "training", null);
                        event.setSessionId(calendarSessionId);
                        return event;
                    });
            calendarEvent.setDate(eventDate);
            calendarEvent.setTitle(session.getTitle());
            calendarEvent.setTotalDistance(totalDistance);
            calendarEvent.setTotalTime(totalTime);
            calendarEvent.setTrainings(calendarTrainings);
            calendarEvent.setType("training");
            calendarEvent.setCompleted(true);
            calendarEvent.setAutoSaved(true);
            calendarEvent.setStartedAt(effectiveStartedAt);
            calendarEvent.setEndedAt(effectiveEndedAt);
            calendarService.saveEvent(calendarEvent);
        } catch (Exception ignored) {
            // 캘린더 저장 실패해도 훈련 완료는 성공으로 처리
        }

        return result;
    }

    private int parseTotalTimeToSeconds(String totalTime) {
        String[] parts = totalTime.split(":");
        if (parts.length == 3) {
            return Integer.parseInt(parts[0]) * 3600 +
                    Integer.parseInt(parts[1]) * 60 +
                    Integer.parseInt(parts[2]);
        }
        return 0;
    }

    private void updateUserStats(String userId, Integer distance, Integer time) {
        if (distance == null) distance = 0;
        if (time == null) time = 0;
        final Integer finalDistance = distance;
        final Integer finalTime = time;

        userStatsRepository.findById(userId).ifPresent(stats -> {
            stats.setTotalSessions(safeInt(stats.getTotalSessions()) + 1);
            stats.setTotalDistance(safeInt(stats.getTotalDistance()) + finalDistance);
            stats.setTotalTime(safeInt(stats.getTotalTime()) + finalTime);
            stats.setMonthlySessions(safeInt(stats.getMonthlySessions()) + 1);
            stats.setMonthlyDistance(safeInt(stats.getMonthlyDistance()) + finalDistance);
            stats.setMonthlyTime(safeInt(stats.getMonthlyTime()) + finalTime);

            // 연속 기록 업데이트
            LocalDate lastDate = stats.getLastTrainingDate();
            if (lastDate != null && lastDate.equals(LocalDate.now().minusDays(1))) {
                stats.setCurrentStreak(safeInt(stats.getCurrentStreak()) + 1);
            } else if (lastDate == null || !lastDate.equals(LocalDate.now())) {
                stats.setCurrentStreak(1);
            }
            if (safeInt(stats.getCurrentStreak()) > safeInt(stats.getLongestStreak())) {
                stats.setLongestStreak(stats.getCurrentStreak());
            }

            stats.setLastTrainingDate(LocalDate.now());
            userStatsRepository.save(stats);
        });
    }

    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }

    @Transactional
    public void deleteSession(UUID sessionId, String userId) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            if (!session.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("본인의 훈련만 삭제할 수 있습니다.");
            }
            sessionRepository.delete(session);
        });
    }

    public Page<TrainingResult> getUserResults(String userId, Pageable pageable) {
        return userRepository.findById(userId)
                .map(user -> resultRepository.findByUserOrderByCompletedAtDesc(user, pageable))
                .orElse(Page.empty());
    }
}

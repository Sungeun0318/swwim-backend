package com.zalmuk.swwim.api.service.training;

import com.zalmuk.swwim.api.dto.watch.WatchWorkoutRequest;
import com.zalmuk.swwim.api.entity.enums.SwimWorkoutType;
import com.zalmuk.swwim.api.entity.training.CalendarEvent;
import com.zalmuk.swwim.api.entity.training.WatchWorkout;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.repository.training.CalendarEventRepository;
import com.zalmuk.swwim.api.repository.training.WatchWorkoutRepository;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class WatchWorkoutService {

    private final WatchWorkoutRepository watchWorkoutRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final UserRepository userRepository;

    public WatchWorkoutService(WatchWorkoutRepository watchWorkoutRepository,
                               CalendarEventRepository calendarEventRepository,
                               UserRepository userRepository) {
        this.watchWorkoutRepository = watchWorkoutRepository;
        this.calendarEventRepository = calendarEventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public WatchWorkout upsertWorkout(String userId, WatchWorkoutRequest request) {
        User user = findUser(userId);
        WatchWorkout workout = watchWorkoutRepository
                .findByUserAndExternalId(user, request.getExternalId())
                .orElseGet(() -> new WatchWorkout(user, request.getSource(), request.getExternalId()));

        workout.setSource(request.getSource());
        workout.setExternalId(request.getExternalId());
        workout.setWorkoutType(request.getWorkoutType() != null ? request.getWorkoutType() : SwimWorkoutType.UNKNOWN);
        workout.setStartedAt(request.getStartedAt());
        workout.setEndedAt(request.getEndedAt());
        workout.setTotalDuration(request.getTotalDuration());
        workout.setTotalDistance(request.getTotalDistance());
        workout.setAvgPaceSecPer100m(request.getAvgPaceSecPer100m());
        workout.setAvgSpeed(request.getAvgSpeed());
        workout.setActiveCalories(request.getActiveCalories());
        workout.setAvgHeartRate(request.getAvgHeartRate());
        workout.setMaxHeartRate(request.getMaxHeartRate());
        workout.setStrokeCount(request.getStrokeCount());
        workout.setSwolf(request.getSwolf());
        workout.setStrokeStyle(request.getStrokeStyle());
        workout.setHeartRateSamples(request.getHeartRateSamples());
        workout.setLaps(request.getLaps());
        workout.setGpsRoute(request.getGpsRoute());
        workout.setRaw(request.getRaw());

        return watchWorkoutRepository.save(workout);
    }

    @Transactional
    public WatchWorkout linkToCalendarEvent(String userId, UUID workoutId, UUID calendarEventId, Double matchConfidence) {
        WatchWorkout workout = findOwnedWorkout(userId, workoutId);
        CalendarEvent calendarEvent = calendarEventRepository.findByIdWithUser(calendarEventId)
                .orElseThrow(() -> new IllegalArgumentException("캘린더 이벤트를 찾을 수 없습니다."));

        if (!calendarEvent.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 캘린더 이벤트에만 연결할 수 있습니다.");
        }

        workout.setCalendarEvent(calendarEvent);
        workout.setMatchConfidence(matchConfidence);
        workout.setMatchedManually(true);
        return watchWorkoutRepository.save(workout);
    }

    @Transactional
    public WatchWorkout unlink(String userId, UUID workoutId) {
        WatchWorkout workout = findOwnedWorkout(userId, workoutId);
        workout.setCalendarEvent(null);
        workout.setMatchConfidence(null);
        workout.setMatchedManually(false);
        return watchWorkoutRepository.save(workout);
    }

    public Optional<WatchWorkout> getByCalendarEvent(String userId, UUID calendarEventId) {
        CalendarEvent calendarEvent = calendarEventRepository.findByIdWithUser(calendarEventId)
                .orElseThrow(() -> new IllegalArgumentException("캘린더 이벤트를 찾을 수 없습니다."));

        if (!calendarEvent.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 캘린더 이벤트만 조회할 수 있습니다.");
        }

        return watchWorkoutRepository.findByCalendarEvent(calendarEvent).stream().findFirst();
    }

    private WatchWorkout findOwnedWorkout(String userId, UUID workoutId) {
        WatchWorkout workout = watchWorkoutRepository.findByIdWithUserAndCalendarEvent(workoutId)
                .orElseThrow(() -> new IllegalArgumentException("워치 운동 기록을 찾을 수 없습니다."));
        if (!workout.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 워치 운동 기록만 수정할 수 있습니다.");
        }
        return workout;
    }

    private User findUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}

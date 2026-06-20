package com.zalmuk.swwim.api.dto.watch;

import com.zalmuk.swwim.api.entity.enums.SwimWorkoutType;
import com.zalmuk.swwim.api.entity.enums.WatchSource;
import com.zalmuk.swwim.api.entity.training.WatchWorkout;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WatchWorkoutResponse {

    private UUID id;
    private String userId;
    private UUID calendarEventId;
    private WatchSource source;
    private String externalId;
    private SwimWorkoutType workoutType;
    private Instant startedAt;
    private Instant endedAt;
    private Integer totalDuration;
    private Integer totalDistance;
    private Double avgPaceSecPer100m;
    private Double avgSpeed;
    private Integer activeCalories;
    private Integer avgHeartRate;
    private Integer maxHeartRate;
    private Integer strokeCount;
    private Integer swolf;
    private String strokeStyle;
    private List<Map<String, Object>> heartRateSamples;
    private List<Map<String, Object>> laps;
    private Map<String, Object> gpsRoute;
    private Map<String, Object> raw;
    private Double matchConfidence;
    private Boolean matchedManually;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WatchWorkoutResponse from(WatchWorkout workout) {
        WatchWorkoutResponse response = new WatchWorkoutResponse();
        response.id = workout.getId();
        response.userId = workout.getUser() != null ? workout.getUser().getId() : null;
        response.calendarEventId = workout.getCalendarEvent() != null ? workout.getCalendarEvent().getId() : null;
        response.source = workout.getSource();
        response.externalId = workout.getExternalId();
        response.workoutType = workout.getWorkoutType();
        response.startedAt = workout.getStartedAt();
        response.endedAt = workout.getEndedAt();
        response.totalDuration = workout.getTotalDuration();
        response.totalDistance = workout.getTotalDistance();
        response.avgPaceSecPer100m = workout.getAvgPaceSecPer100m();
        response.avgSpeed = workout.getAvgSpeed();
        response.activeCalories = workout.getActiveCalories();
        response.avgHeartRate = workout.getAvgHeartRate();
        response.maxHeartRate = workout.getMaxHeartRate();
        response.strokeCount = workout.getStrokeCount();
        response.swolf = workout.getSwolf();
        response.strokeStyle = workout.getStrokeStyle();
        response.heartRateSamples = workout.getHeartRateSamples();
        response.laps = workout.getLaps();
        response.gpsRoute = workout.getGpsRoute();
        response.raw = workout.getRaw();
        response.matchConfidence = workout.getMatchConfidence();
        response.matchedManually = workout.getMatchedManually();
        response.createdAt = workout.getCreatedAt();
        response.updatedAt = workout.getUpdatedAt();
        return response;
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public UUID getCalendarEventId() {
        return calendarEventId;
    }

    public WatchSource getSource() {
        return source;
    }

    public String getExternalId() {
        return externalId;
    }

    public SwimWorkoutType getWorkoutType() {
        return workoutType;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public Integer getTotalDistance() {
        return totalDistance;
    }

    public Double getAvgPaceSecPer100m() {
        return avgPaceSecPer100m;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public Integer getActiveCalories() {
        return activeCalories;
    }

    public Integer getAvgHeartRate() {
        return avgHeartRate;
    }

    public Integer getMaxHeartRate() {
        return maxHeartRate;
    }

    public Integer getStrokeCount() {
        return strokeCount;
    }

    public Integer getSwolf() {
        return swolf;
    }

    public String getStrokeStyle() {
        return strokeStyle;
    }

    public List<Map<String, Object>> getHeartRateSamples() {
        return heartRateSamples;
    }

    public List<Map<String, Object>> getLaps() {
        return laps;
    }

    public Map<String, Object> getGpsRoute() {
        return gpsRoute;
    }

    public Map<String, Object> getRaw() {
        return raw;
    }

    public Double getMatchConfidence() {
        return matchConfidence;
    }

    public Boolean getMatchedManually() {
        return matchedManually;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

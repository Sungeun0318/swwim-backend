package com.zalmuk.swwim.api.dto.watch;

import com.zalmuk.swwim.api.entity.enums.SwimWorkoutType;
import com.zalmuk.swwim.api.entity.enums.WatchSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class WatchWorkoutRequest {

    @NotNull(message = "source는 필수입니다.")
    private WatchSource source;

    @NotBlank(message = "externalId는 필수입니다.")
    private String externalId;

    private SwimWorkoutType workoutType;
    private Long startedAtEpochMs;
    private Long endedAtEpochMs;
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

    public WatchSource getSource() {
        return source;
    }

    public void setSource(WatchSource source) {
        this.source = source;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public SwimWorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(SwimWorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public Long getStartedAtEpochMs() {
        return startedAtEpochMs;
    }

    public void setStartedAtEpochMs(Long startedAtEpochMs) {
        this.startedAtEpochMs = startedAtEpochMs;
    }

    public Long getEndedAtEpochMs() {
        return endedAtEpochMs;
    }

    public void setEndedAtEpochMs(Long endedAtEpochMs) {
        this.endedAtEpochMs = endedAtEpochMs;
    }

    public Instant getStartedAt() {
        return startedAtEpochMs != null ? Instant.ofEpochMilli(startedAtEpochMs) : null;
    }

    public Instant getEndedAt() {
        return endedAtEpochMs != null ? Instant.ofEpochMilli(endedAtEpochMs) : null;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Integer getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Integer totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Double getAvgPaceSecPer100m() {
        return avgPaceSecPer100m;
    }

    public void setAvgPaceSecPer100m(Double avgPaceSecPer100m) {
        this.avgPaceSecPer100m = avgPaceSecPer100m;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Integer getActiveCalories() {
        return activeCalories;
    }

    public void setActiveCalories(Integer activeCalories) {
        this.activeCalories = activeCalories;
    }

    public Integer getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(Integer avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }

    public Integer getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(Integer maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    public Integer getStrokeCount() {
        return strokeCount;
    }

    public void setStrokeCount(Integer strokeCount) {
        this.strokeCount = strokeCount;
    }

    public Integer getSwolf() {
        return swolf;
    }

    public void setSwolf(Integer swolf) {
        this.swolf = swolf;
    }

    public String getStrokeStyle() {
        return strokeStyle;
    }

    public void setStrokeStyle(String strokeStyle) {
        this.strokeStyle = strokeStyle;
    }

    public List<Map<String, Object>> getHeartRateSamples() {
        return heartRateSamples;
    }

    public void setHeartRateSamples(List<Map<String, Object>> heartRateSamples) {
        this.heartRateSamples = heartRateSamples;
    }

    public List<Map<String, Object>> getLaps() {
        return laps;
    }

    public void setLaps(List<Map<String, Object>> laps) {
        this.laps = laps;
    }

    public Map<String, Object> getGpsRoute() {
        return gpsRoute;
    }

    public void setGpsRoute(Map<String, Object> gpsRoute) {
        this.gpsRoute = gpsRoute;
    }

    public Map<String, Object> getRaw() {
        return raw;
    }

    public void setRaw(Map<String, Object> raw) {
        this.raw = raw;
    }
}

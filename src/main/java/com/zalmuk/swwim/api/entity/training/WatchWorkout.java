package com.zalmuk.swwim.api.entity.training;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.enums.SwimWorkoutType;
import com.zalmuk.swwim.api.entity.enums.WatchSource;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "watch_workouts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_watch_workouts_user_external", columnNames = {"user_id", "external_id"})
        },
        indexes = {
                @Index(name = "idx_watch_workouts_user_external", columnList = "user_id, external_id"),
                @Index(name = "idx_watch_workouts_calendar_event", columnList = "calendar_event_id")
        })
public class WatchWorkout extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_event_id")
    private CalendarEvent calendarEvent;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private WatchSource source;

    @Column(name = "external_id", nullable = false, length = 255)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "workout_type", length = 30)
    private SwimWorkoutType workoutType = SwimWorkoutType.UNKNOWN;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "total_duration")
    private Integer totalDuration;

    @Column(name = "total_distance")
    private Integer totalDistance;

    @Column(name = "avg_pace_sec_per_100m")
    private Double avgPaceSecPer100m;

    @Column(name = "avg_speed")
    private Double avgSpeed;

    @Column(name = "active_calories")
    private Integer activeCalories;

    @Column(name = "avg_heart_rate")
    private Integer avgHeartRate;

    @Column(name = "max_heart_rate")
    private Integer maxHeartRate;

    @Column(name = "stroke_count")
    private Integer strokeCount;

    @Column(name = "swolf")
    private Integer swolf;

    @Column(name = "stroke_style", length = 30)
    private String strokeStyle;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "heart_rate_samples", columnDefinition = "jsonb")
    private List<Map<String, Object>> heartRateSamples;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "laps", columnDefinition = "jsonb")
    private List<Map<String, Object>> laps;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "gps_route", columnDefinition = "jsonb")
    private Map<String, Object> gpsRoute;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw", columnDefinition = "jsonb")
    private Map<String, Object> raw;

    @Column(name = "match_confidence")
    private Double matchConfidence;

    @Column(name = "matched_manually")
    private Boolean matchedManually = false;

    protected WatchWorkout() {
    }

    public WatchWorkout(User user, WatchSource source, String externalId) {
        this.user = user;
        this.source = source;
        this.externalId = externalId;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CalendarEvent getCalendarEvent() {
        return calendarEvent;
    }

    public void setCalendarEvent(CalendarEvent calendarEvent) {
        this.calendarEvent = calendarEvent;
    }

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

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
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

    public Double getMatchConfidence() {
        return matchConfidence;
    }

    public void setMatchConfidence(Double matchConfidence) {
        this.matchConfidence = matchConfidence;
    }

    public Boolean getMatchedManually() {
        return matchedManually;
    }

    public void setMatchedManually(Boolean matchedManually) {
        this.matchedManually = matchedManually;
    }
}

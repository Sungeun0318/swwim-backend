package com.zalmuk.swwim.api.dto.watch;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class WatchWorkoutLinkRequest {

    @NotNull(message = "calendarEventId는 필수입니다.")
    private UUID calendarEventId;

    private Double matchConfidence;

    public UUID getCalendarEventId() {
        return calendarEventId;
    }

    public void setCalendarEventId(UUID calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public Double getMatchConfidence() {
        return matchConfidence;
    }

    public void setMatchConfidence(Double matchConfidence) {
        this.matchConfidence = matchConfidence;
    }
}

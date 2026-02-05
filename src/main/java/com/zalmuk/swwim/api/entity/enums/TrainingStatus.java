package com.zalmuk.swwim.api.entity.enums;

/**
 * 훈련 세션 상태
 */
public enum TrainingStatus {
    CREATED("created"),
    IN_PROGRESS("in_progress"),
    PAUSED("paused"),
    COMPLETED("completed");

    private final String value;

    TrainingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TrainingStatus fromValue(String value) {
        for (TrainingStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown training status: " + value);
    }
}

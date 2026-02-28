package com.zalmuk.swwim.api.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 알림 유형
 */
public enum NotificationType {
    TRAINING_REMINDER("training_reminder"),
    FOLLOW("follow"),
    LIKE("like"),
    COMMENT("comment"),
    ACHIEVEMENT("achievement");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static NotificationType fromValue(String value) {
        for (NotificationType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown notification type: " + value);
    }
}

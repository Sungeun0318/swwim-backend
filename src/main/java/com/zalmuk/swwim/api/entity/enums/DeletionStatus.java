package com.zalmuk.swwim.api.entity.enums;

/**
 * 탈퇴 요청 상태
 */
public enum DeletionStatus {
    PENDING("pending"),
    CANCELLED("cancelled"),
    COMPLETED("completed");

    private final String value;

    DeletionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DeletionStatus fromValue(String value) {
        for (DeletionStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown deletion status: " + value);
    }
}

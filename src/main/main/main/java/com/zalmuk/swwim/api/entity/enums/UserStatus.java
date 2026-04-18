package com.zalmuk.swwim.api.entity.enums;

/**
 * 사용자 계정 상태
 */
public enum UserStatus {
    ACTIVE("active"),
    SUSPENDED("suspended"),
    DELETED("deleted");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserStatus fromValue(String value) {
        for (UserStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}

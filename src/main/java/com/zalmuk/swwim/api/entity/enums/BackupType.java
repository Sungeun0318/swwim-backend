package com.zalmuk.swwim.api.entity.enums;

/**
 * 백업 유형
 */
public enum BackupType {
    AUTO("auto"),
    MANUAL("manual");

    private final String value;

    BackupType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BackupType fromValue(String value) {
        for (BackupType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown backup type: " + value);
    }
}

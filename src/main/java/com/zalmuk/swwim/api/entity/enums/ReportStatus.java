package com.zalmuk.swwim.api.entity.enums;

/**
 * 신고 처리 상태
 */
public enum ReportStatus {
    PENDING("pending"),
    REVIEWING("reviewing"),
    RESOLVED("resolved"),
    DISMISSED("dismissed");

    private final String value;

    ReportStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ReportStatus fromValue(String value) {
        for (ReportStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown report status: " + value);
    }
}

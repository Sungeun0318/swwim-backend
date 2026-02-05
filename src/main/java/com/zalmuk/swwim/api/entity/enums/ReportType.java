package com.zalmuk.swwim.api.entity.enums;

/**
 * 신고 유형
 */
public enum ReportType {
    POST("post"),
    COMMENT("comment"),
    USER("user"),
    CHAT("chat");

    private final String value;

    ReportType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ReportType fromValue(String value) {
        for (ReportType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown report type: " + value);
    }
}

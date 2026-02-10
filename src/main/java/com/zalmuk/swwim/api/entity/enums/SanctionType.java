package com.zalmuk.swwim.api.entity.enums;

/**
 * 제재 유형
 */
public enum SanctionType {
    WARNING("warning"),
    SUSPENSION("suspension"),
    BAN("ban");

    private final String value;

    SanctionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SanctionType fromValue(String value) {
        for (SanctionType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown sanction type: " + value);
    }
}

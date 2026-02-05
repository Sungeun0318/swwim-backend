package com.zalmuk.swwim.api.entity.enums;

/**
 * 구독 유형
 */
public enum SubscriptionType {
    MONTHLY("monthly"),
    YEARLY("yearly");

    private final String value;

    SubscriptionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SubscriptionType fromValue(String value) {
        for (SubscriptionType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown subscription type: " + value);
    }
}

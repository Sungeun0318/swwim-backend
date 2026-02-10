package com.zalmuk.swwim.api.entity.enums;

/**
 * 프로필 공개 범위
 */
public enum ProfileVisibility {
    PUBLIC("public"),
    FRIENDS("friends"),
    PRIVATE("private");

    private final String value;

    ProfileVisibility(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProfileVisibility fromValue(String value) {
        for (ProfileVisibility visibility : values()) {
            if (visibility.value.equals(value)) {
                return visibility;
            }
        }
        throw new IllegalArgumentException("Unknown visibility: " + value);
    }
}

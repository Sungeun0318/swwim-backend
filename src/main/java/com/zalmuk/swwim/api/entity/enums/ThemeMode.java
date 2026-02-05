package com.zalmuk.swwim.api.entity.enums;

/**
 * 앱 테마 모드
 */
public enum ThemeMode {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    private final String value;

    ThemeMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ThemeMode fromValue(String value) {
        for (ThemeMode mode : values()) {
            if (mode.value.equals(value)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown theme mode: " + value);
    }
}

package com.zalmuk.swwim.api.entity.enums;

/**
 * 소셜 로그인 제공자
 */
public enum AuthProvider {
    GOOGLE("google"),
    APPLE("apple"),
    NAVER("naver"),
    EMAIL("email");

    private final String value;

    AuthProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AuthProvider fromValue(String value) {
        for (AuthProvider provider : values()) {
            if (provider.value.equals(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown provider: " + value);
    }
}

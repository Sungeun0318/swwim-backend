package com.zalmuk.swwim.api.entity.enums;

/**
 * 사용자 역할 (보안 레이어)
 */
public enum UserRole {
    USER("USER"),
    ADMIN("ADMIN");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean hasAccess(UserRole required) {
        return this.ordinal() >= required.ordinal();
    }
}

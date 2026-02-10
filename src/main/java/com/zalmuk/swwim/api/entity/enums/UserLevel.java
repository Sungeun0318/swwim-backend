package com.zalmuk.swwim.api.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 사용자 수영 레벨
 */
public enum UserLevel {
    BEGINNER("입문"),
    INTERMEDIATE("중급"),
    ADVANCED("상급"),
    MASTER("마스터");

    private final String koreanName;

    UserLevel(String koreanName) {
        this.koreanName = koreanName;
    }

    @JsonValue
    public String getKoreanName() {
        return koreanName;
    }

    @JsonCreator
    public static UserLevel fromValue(String value) {
        if (value == null) {
            return null;
        }
        // 영어 enum 이름으로 찾기
        for (UserLevel level : values()) {
            if (level.name().equalsIgnoreCase(value)) {
                return level;
            }
        }
        // 한글 이름으로 찾기
        for (UserLevel level : values()) {
            if (level.koreanName.equals(value)) {
                return level;
            }
        }
        // 추가 매핑 (초급 = 입문)
        if ("초급".equals(value)) {
            return BEGINNER;
        }
        throw new IllegalArgumentException("Unknown level: " + value);
    }
}

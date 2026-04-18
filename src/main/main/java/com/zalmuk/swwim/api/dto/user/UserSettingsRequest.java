package com.zalmuk.swwim.api.dto.user;

import com.zalmuk.swwim.api.entity.enums.ProfileVisibility;
import com.zalmuk.swwim.api.entity.enums.ThemeMode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

/**
 * 사용자 설정 요청 DTO
 */
@Schema(description = "사용자 설정 요청")
public class UserSettingsRequest {

    // 알림 설정
    @Schema(description = "푸시 알림 활성화")
    private Boolean enablePushNotifications;

    @Schema(description = "훈련 리마인더 활성화")
    private Boolean enableTrainingReminders;

    @Schema(description = "리마인더 시간")
    private LocalTime reminderTime;

    // 앱 설정
    @Schema(description = "테마 모드")
    private ThemeMode theme;

    @Schema(description = "언어 (ko, en)")
    private String language;

    // 훈련 설정
    @Schema(description = "기본 비프음")
    private String defaultBeepSound;

    @Schema(description = "기본 인원 수")
    private Integer defaultNumPeople;

    @Schema(description = "자동 저장")
    private Boolean autoSaveTraining;

    // 개인정보 설정
    @Schema(description = "프로필 공개 범위")
    private ProfileVisibility profileVisibility;

    @Schema(description = "통계 공개 여부")
    private Boolean showStats;

    // Constructors
    public UserSettingsRequest() {
    }

    // Getters and Setters
    public Boolean getEnablePushNotifications() {
        return enablePushNotifications;
    }

    public void setEnablePushNotifications(Boolean enablePushNotifications) {
        this.enablePushNotifications = enablePushNotifications;
    }

    public Boolean getEnableTrainingReminders() {
        return enableTrainingReminders;
    }

    public void setEnableTrainingReminders(Boolean enableTrainingReminders) {
        this.enableTrainingReminders = enableTrainingReminders;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public ThemeMode getTheme() {
        return theme;
    }

    public void setTheme(ThemeMode theme) {
        this.theme = theme;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDefaultBeepSound() {
        return defaultBeepSound;
    }

    public void setDefaultBeepSound(String defaultBeepSound) {
        this.defaultBeepSound = defaultBeepSound;
    }

    public Integer getDefaultNumPeople() {
        return defaultNumPeople;
    }

    public void setDefaultNumPeople(Integer defaultNumPeople) {
        this.defaultNumPeople = defaultNumPeople;
    }

    public Boolean getAutoSaveTraining() {
        return autoSaveTraining;
    }

    public void setAutoSaveTraining(Boolean autoSaveTraining) {
        this.autoSaveTraining = autoSaveTraining;
    }

    public ProfileVisibility getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(ProfileVisibility profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    public Boolean getShowStats() {
        return showStats;
    }

    public void setShowStats(Boolean showStats) {
        this.showStats = showStats;
    }
}

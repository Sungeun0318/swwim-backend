package com.zalmuk.swwim.api.dto.user;

import com.zalmuk.swwim.api.entity.enums.ProfileVisibility;
import com.zalmuk.swwim.api.entity.enums.ThemeMode;
import com.zalmuk.swwim.api.entity.user.UserSettings;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 사용자 설정 응답 DTO
 */
@Schema(description = "사용자 설정 응답")
public class UserSettingsResponse {

    @Schema(description = "사용자 ID")
    private String userId;

    // 알림 설정
    @Schema(description = "푸시 알림 활성화")
    private Boolean enablePushNotifications;

    @Schema(description = "훈련 리마인더 활성화")
    private Boolean enableTrainingReminders;

    @Schema(description = "좋아요 알림 활성화")
    private Boolean notifyLike;

    @Schema(description = "댓글 알림 활성화")
    private Boolean notifyComment;

    @Schema(description = "팔로우 알림 활성화")
    private Boolean notifyFollow;

    @Schema(description = "업적 알림 활성화")
    private Boolean notifyAchievement;

    @Schema(description = "시스템 알림 활성화")
    private Boolean notifySystem;

    @Schema(description = "마케팅 알림 활성화")
    private Boolean notifyMarketing;

    @Schema(description = "마케팅 수신 동의 시각")
    private LocalDateTime marketingAgreedAt;

    @Schema(description = "방해 금지 시작 시각")
    private LocalTime quietHoursStart;

    @Schema(description = "방해 금지 종료 시각")
    private LocalTime quietHoursEnd;

    @Schema(description = "리마인더 시간")
    private LocalTime reminderTime;

    // 앱 설정
    @Schema(description = "테마 모드")
    private ThemeMode theme;

    @Schema(description = "언어")
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
    public UserSettingsResponse() {
    }

    public static UserSettingsResponse from(UserSettings settings) {
        UserSettingsResponse response = new UserSettingsResponse();
        response.setUserId(settings.getUserId());
        response.setEnablePushNotifications(settings.getEnablePushNotifications());
        response.setEnableTrainingReminders(settings.getEnableTrainingReminders());
        response.setNotifyLike(settings.getNotifyLike());
        response.setNotifyComment(settings.getNotifyComment());
        response.setNotifyFollow(settings.getNotifyFollow());
        response.setNotifyAchievement(settings.getNotifyAchievement());
        response.setNotifySystem(settings.getNotifySystem());
        response.setNotifyMarketing(settings.getNotifyMarketing());
        response.setMarketingAgreedAt(settings.getMarketingAgreedAt());
        response.setQuietHoursStart(settings.getQuietHoursStart());
        response.setQuietHoursEnd(settings.getQuietHoursEnd());
        response.setReminderTime(settings.getReminderTime());
        response.setTheme(settings.getTheme());
        response.setLanguage(settings.getLanguage());
        response.setDefaultBeepSound(settings.getDefaultBeepSound());
        response.setDefaultNumPeople(settings.getDefaultNumPeople());
        response.setAutoSaveTraining(settings.getAutoSaveTraining());
        response.setProfileVisibility(settings.getProfileVisibility());
        response.setShowStats(settings.getShowStats());
        return response;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public Boolean getNotifyLike() {
        return notifyLike;
    }

    public void setNotifyLike(Boolean notifyLike) {
        this.notifyLike = notifyLike;
    }

    public Boolean getNotifyComment() {
        return notifyComment;
    }

    public void setNotifyComment(Boolean notifyComment) {
        this.notifyComment = notifyComment;
    }

    public Boolean getNotifyFollow() {
        return notifyFollow;
    }

    public void setNotifyFollow(Boolean notifyFollow) {
        this.notifyFollow = notifyFollow;
    }

    public Boolean getNotifyAchievement() {
        return notifyAchievement;
    }

    public void setNotifyAchievement(Boolean notifyAchievement) {
        this.notifyAchievement = notifyAchievement;
    }

    public Boolean getNotifySystem() {
        return notifySystem;
    }

    public void setNotifySystem(Boolean notifySystem) {
        this.notifySystem = notifySystem;
    }

    public Boolean getNotifyMarketing() {
        return notifyMarketing;
    }

    public void setNotifyMarketing(Boolean notifyMarketing) {
        this.notifyMarketing = notifyMarketing;
    }

    public LocalDateTime getMarketingAgreedAt() {
        return marketingAgreedAt;
    }

    public void setMarketingAgreedAt(LocalDateTime marketingAgreedAt) {
        this.marketingAgreedAt = marketingAgreedAt;
    }

    public LocalTime getQuietHoursStart() {
        return quietHoursStart;
    }

    public void setQuietHoursStart(LocalTime quietHoursStart) {
        this.quietHoursStart = quietHoursStart;
    }

    public LocalTime getQuietHoursEnd() {
        return quietHoursEnd;
    }

    public void setQuietHoursEnd(LocalTime quietHoursEnd) {
        this.quietHoursEnd = quietHoursEnd;
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

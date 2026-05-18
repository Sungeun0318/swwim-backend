package com.zalmuk.swwim.api.entity.user;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.enums.ProfileVisibility;
import com.zalmuk.swwim.api.entity.enums.ThemeMode;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 사용자 설정
 */
@Entity
@Table(name = "user_settings")
public class UserSettings extends BaseEntity {

    @Id
    @Column(name = "user_id", length = 128)
    private String userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    // 알림 설정
    @Column(name = "enable_push_notifications")
    private Boolean enablePushNotifications = true;

    @Column(name = "enable_training_reminders")
    private Boolean enableTrainingReminders = true;

    @Column(name = "notify_like")
    private Boolean notifyLike = true;

    @Column(name = "notify_comment")
    private Boolean notifyComment = true;

    @Column(name = "notify_follow")
    private Boolean notifyFollow = true;

    @Column(name = "notify_achievement")
    private Boolean notifyAchievement = true;

    @Column(name = "notify_system")
    private Boolean notifySystem = true;

    @Column(name = "notify_marketing")
    private Boolean notifyMarketing = false;

    @Column(name = "marketing_agreed_at")
    private LocalDateTime marketingAgreedAt;

    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart;

    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd;

    @Column(name = "reminder_time")
    private LocalTime reminderTime = LocalTime.of(9, 0);

    // 앱 설정
    @Enumerated(EnumType.STRING)
    @Column(name = "theme", length = 20)
    private ThemeMode theme = ThemeMode.SYSTEM;

    @Column(name = "language", length = 10)
    private String language = "ko";

    // 훈련 설정
    @Column(name = "default_beep_sound", length = 50)
    private String defaultBeepSound = "beep1";

    @Column(name = "default_num_people")
    private Integer defaultNumPeople = 1;

    @Column(name = "auto_save_training")
    private Boolean autoSaveTraining = true;

    // 개인정보 설정
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_visibility", length = 20)
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;

    @Column(name = "show_stats")
    private Boolean showStats = true;

    // Deprecated: user_push_tokens 테이블로 이관. 다음 단계까지 하위 호환을 위해 유지.
    @Column(name = "fcm_token", columnDefinition = "TEXT")
    private String fcmToken;

    // Constructors
    protected UserSettings() {
    }

    public UserSettings(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
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
        this.notifySystem = true;
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

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}

package com.zalmuk.swwim.api.entity.user;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.enums.ProfileVisibility;
import com.zalmuk.swwim.api.entity.enums.ThemeMode;
import jakarta.persistence.*;

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

    // FCM 토큰 (푸시 알림)
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

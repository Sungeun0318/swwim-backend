package com.zalmuk.swwim.api.entity.user;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.enums.AuthProvider;
import com.zalmuk.swwim.api.entity.enums.SubscriptionType;
import com.zalmuk.swwim.api.entity.enums.UserLevel;
import com.zalmuk.swwim.api.entity.enums.UserStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 사용자 기본 정보
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_status", columnList = "status"),
        @Index(name = "idx_users_premium", columnList = "is_premium"),
        @Index(name = "idx_users_created_at", columnList = "created_at")
})
public class User extends BaseEntity {

    @Id
    @Column(name = "id", length = 128)
    private String id; // Firebase Auth UID

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    // 소셜 로그인 정보
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_id")
    private String providerId;

    // 이메일 로그인용 비밀번호 (소셜 로그인은 null)
    @Column(name = "password")
    private String password;

    // 프로필 정보
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", length = 20)
    private UserLevel level;

    @Column(name = "swim_style", length = 50)
    private String swimStyle;

    // 통계 카운터
    @Column(name = "followers_count")
    private Integer followersCount = 0;

    @Column(name = "following_count")
    private Integer followingCount = 0;

    @Column(name = "post_count")
    private Integer postCount = 0;

    // 수영장 정보
    @Column(name = "selected_pool_id")
    private String selectedPoolId;

    // 구독 정보
    @Column(name = "is_premium")
    private Boolean isPremium = false;

    @Column(name = "premium_started_at")
    private LocalDateTime premiumStartedAt;

    @Column(name = "premium_expires_at")
    private LocalDateTime premiumExpiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", length = 20)
    private SubscriptionType subscriptionType;

    // 관리자 플래그
    @Column(name = "is_admin")
    private Boolean isAdmin = false;

    // 계정 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "deletion_requested_at")
    private LocalDateTime deletionRequestedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // 연관관계
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserSettings settings;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserStats stats;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserFollow> following = new HashSet<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserFollow> followers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BlockedUser> blockedUsers = new HashSet<>();

    // Constructors
    protected User() {
    }

    public User(String id, String email, AuthProvider provider) {
        this.id = id;
        this.email = email;
        this.provider = provider;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public UserLevel getLevel() {
        return level;
    }

    public void setLevel(UserLevel level) {
        this.level = level;
    }

    public String getSwimStyle() {
        return swimStyle;
    }

    public void setSwimStyle(String swimStyle) {
        this.swimStyle = swimStyle;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public String getSelectedPoolId() {
        return selectedPoolId;
    }

    public void setSelectedPoolId(String selectedPoolId) {
        this.selectedPoolId = selectedPoolId;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

    public LocalDateTime getPremiumStartedAt() {
        return premiumStartedAt;
    }

    public void setPremiumStartedAt(LocalDateTime premiumStartedAt) {
        this.premiumStartedAt = premiumStartedAt;
    }

    public LocalDateTime getPremiumExpiresAt() {
        return premiumExpiresAt;
    }

    public void setPremiumExpiresAt(LocalDateTime premiumExpiresAt) {
        this.premiumExpiresAt = premiumExpiresAt;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getDeletionRequestedAt() {
        return deletionRequestedAt;
    }

    public void setDeletionRequestedAt(LocalDateTime deletionRequestedAt) {
        this.deletionRequestedAt = deletionRequestedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
        if (settings != null) {
            settings.setUser(this);
        }
    }

    public UserStats getStats() {
        return stats;
    }

    public void setStats(UserStats stats) {
        this.stats = stats;
        if (stats != null) {
            stats.setUser(this);
        }
    }

    public Set<UserFollow> getFollowing() {
        return following;
    }

    public Set<UserFollow> getFollowers() {
        return followers;
    }

    public Set<BlockedUser> getBlockedUsers() {
        return blockedUsers;
    }
}

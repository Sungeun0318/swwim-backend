package com.zalmuk.swwim.api.dto.user;

import com.zalmuk.swwim.api.entity.enums.AuthProvider;
import com.zalmuk.swwim.api.entity.enums.SubscriptionType;
import com.zalmuk.swwim.api.entity.enums.UserLevel;
import com.zalmuk.swwim.api.entity.enums.UserStatus;
import com.zalmuk.swwim.api.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO
 */
@Schema(description = "사용자 정보 응답")
public class UserResponse {

    @Schema(description = "사용자 ID")
    private String id;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "자기소개")
    private String bio;

    @Schema(description = "수영 레벨")
    private UserLevel level;

    @Schema(description = "선호 수영 스타일")
    private String swimStyle;

    @Schema(description = "팔로워 수")
    private Integer followersCount;

    @Schema(description = "팔로잉 수")
    private Integer followingCount;

    @Schema(description = "게시글 수")
    private Integer postCount;

    @Schema(description = "인증 제공자")
    private AuthProvider provider;

    @Schema(description = "프리미엄 여부")
    private Boolean isPremium;

    @Schema(description = "구독 타입")
    private SubscriptionType subscriptionType;

    @Schema(description = "계정 상태")
    private UserStatus status;

    @Schema(description = "마지막 로그인 시간")
    private LocalDateTime lastLoginAt;

    @Schema(description = "가입 일시")
    private LocalDateTime createdAt;

    // Constructors
    public UserResponse() {
    }

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setNickname(user.getNickname());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setBio(user.getBio());
        response.setLevel(user.getLevel());
        response.setSwimStyle(user.getSwimStyle());
        response.setFollowersCount(user.getFollowersCount());
        response.setFollowingCount(user.getFollowingCount());
        response.setPostCount(user.getPostCount());
        response.setProvider(user.getProvider());
        response.setIsPremium(user.getIsPremium());
        response.setSubscriptionType(user.getSubscriptionType());
        response.setStatus(user.getStatus());
        response.setLastLoginAt(user.getLastLoginAt());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public static UserResponse summary(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        // nickname이 없으면 name으로 폴백
        String nickname = user.getNickname();
        response.setNickname(nickname != null && !nickname.isBlank() ? nickname : user.getName());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setLevel(user.getLevel());
        return response;
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

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

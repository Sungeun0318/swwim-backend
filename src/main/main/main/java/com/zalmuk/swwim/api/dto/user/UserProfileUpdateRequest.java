package com.zalmuk.swwim.api.dto.user;

import com.zalmuk.swwim.api.entity.enums.UserLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * 사용자 프로필 수정 요청 DTO
 */
@Schema(description = "프로필 수정 요청")
public class UserProfileUpdateRequest {

    @Size(max = 100, message = "이름은 100자를 초과할 수 없습니다")
    @Schema(description = "이름")
    private String name;

    @Size(max = 50, message = "닉네임은 50자를 초과할 수 없습니다")
    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;

    @Size(max = 500, message = "자기소개는 500자를 초과할 수 없습니다")
    @Schema(description = "자기소개")
    private String bio;

    @Schema(description = "수영 레벨")
    private UserLevel level;

    @Size(max = 50, message = "수영 스타일은 50자를 초과할 수 없습니다")
    @Schema(description = "선호 수영 스타일")
    private String swimStyle;

    @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다")
    @Schema(description = "전화번호")
    private String phoneNumber;

    // Constructors
    public UserProfileUpdateRequest() {
    }

    // Getters and Setters
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

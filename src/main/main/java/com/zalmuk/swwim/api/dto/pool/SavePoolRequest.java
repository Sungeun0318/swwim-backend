package com.zalmuk.swwim.api.dto.pool;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 수영장 저장 요청 DTO (Google Places 데이터 포함)
 */
@Schema(description = "수영장 저장 요청")
public class SavePoolRequest {

    @Schema(description = "Google Place ID", example = "ChIJ...")
    @NotBlank(message = "placeId는 필수입니다")
    private String placeId;

    @Schema(description = "수영장 이름", example = "올림픽 수영장")
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @Schema(description = "주소", example = "서울시 송파구...")
    @NotBlank(message = "주소는 필수입니다")
    private String address;

    @Schema(description = "위도", example = "37.5665")
    @NotNull(message = "위도는 필수입니다")
    private BigDecimal latitude;

    @Schema(description = "경도", example = "126.9780")
    @NotNull(message = "경도는 필수입니다")
    private BigDecimal longitude;

    @Schema(description = "평점", example = "4.5")
    private BigDecimal rating;

    @Schema(description = "리뷰 수", example = "100")
    private Integer userRatingsTotal;

    @Schema(description = "사진 URL")
    private String photoUrl;

    @Schema(description = "전화번호")
    private String phoneNumber;

    @Schema(description = "웹사이트")
    private String website;

    // Getters and Setters
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public void setUserRatingsTotal(Integer userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}

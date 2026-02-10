package com.zalmuk.swwim.api.dto.pool;

import com.zalmuk.swwim.api.entity.pool.SwimmingPool;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 수영장 응답 DTO
 */
@Schema(description = "수영장 정보 응답")
public class SwimmingPoolResponse {

    @Schema(description = "수영장 ID")
    private Integer id;

    @Schema(description = "Google Place ID")
    private String placeId;

    @Schema(description = "수영장 이름")
    private String name;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "위도")
    private BigDecimal latitude;

    @Schema(description = "경도")
    private BigDecimal longitude;

    @Schema(description = "Google Places 평점")
    private BigDecimal rating;

    @Schema(description = "총 평가 수")
    private Integer userRatingsTotal;

    @Schema(description = "사진 URL")
    private String photoUrl;

    @Schema(description = "전화번호")
    private String phoneNumber;

    @Schema(description = "웹사이트")
    private String website;

    @Schema(description = "운영 시간")
    private Map<String, Object> openingHours;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "즐겨찾기 여부")
    private Boolean isSaved;

    // Constructors
    public SwimmingPoolResponse() {
    }

    public static SwimmingPoolResponse from(SwimmingPool pool) {
        SwimmingPoolResponse response = new SwimmingPoolResponse();
        response.setId(pool.getId());
        response.setPlaceId(pool.getPlaceId());
        response.setName(pool.getName());
        response.setAddress(pool.getAddress());
        response.setLatitude(pool.getLatitude());
        response.setLongitude(pool.getLongitude());
        response.setRating(pool.getRating());
        response.setUserRatingsTotal(pool.getUserRatingsTotal());
        response.setPhotoUrl(pool.getPhotoUrl());
        response.setPhoneNumber(pool.getPhoneNumber());
        response.setWebsite(pool.getWebsite());
        response.setOpeningHours(pool.getOpeningHours());
        response.setCreatedAt(pool.getCreatedAt());
        return response;
    }

    public static SwimmingPoolResponse summary(SwimmingPool pool) {
        SwimmingPoolResponse response = new SwimmingPoolResponse();
        response.setId(pool.getId());
        response.setName(pool.getName());
        response.setAddress(pool.getAddress());
        response.setRating(pool.getRating());
        response.setPhotoUrl(pool.getPhotoUrl());
        return response;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Map<String, Object> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(Map<String, Object> openingHours) {
        this.openingHours = openingHours;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsSaved() {
        return isSaved;
    }

    public void setIsSaved(Boolean isSaved) {
        this.isSaved = isSaved;
    }
}

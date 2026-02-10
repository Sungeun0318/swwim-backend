package com.zalmuk.swwim.api.entity.pool;

import com.zalmuk.swwim.api.entity.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 수영장 정보
 */
@Entity
@Table(name = "swimming_pools", indexes = {
        @Index(name = "idx_pools_place_id", columnList = "place_id"),
        @Index(name = "idx_pools_location", columnList = "latitude, longitude")
})
public class SwimmingPool extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "place_id", nullable = false, unique = true)
    private String placeId; // Google Place ID

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    // 위치 정보
    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    // Google Places 정보
    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "user_ratings_total")
    private Integer userRatingsTotal;

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "website", columnDefinition = "TEXT")
    private String website;

    // 운영 시간 (JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "opening_hours", columnDefinition = "jsonb")
    private Map<String, Object> openingHours;

    // 정렬 순서
    @Column(name = "display_order")
    private Integer displayOrder = 0;

    // 연관관계
    @OneToMany(mappedBy = "pool", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PoolReview> reviews = new HashSet<>();

    @OneToMany(mappedBy = "pool", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SavedPool> savedByUsers = new HashSet<>();

    // Constructors
    protected SwimmingPool() {
    }

    public SwimmingPool(String placeId, String name, String address, BigDecimal latitude, BigDecimal longitude) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Set<PoolReview> getReviews() {
        return reviews;
    }

    public Set<SavedPool> getSavedByUsers() {
        return savedByUsers;
    }
}

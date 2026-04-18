package com.zalmuk.swwim.api.entity.pool;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 수영장 리뷰
 */
@Entity
@Table(name = "pool_reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"pool_id", "user_id"}),
        indexes = {
                @Index(name = "idx_pool_reviews_pool", columnList = "pool_id"),
                @Index(name = "idx_pool_reviews_user", columnList = "user_id")
        })
public class PoolReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pool_id", nullable = false)
    private SwimmingPool pool;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rating", nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    private String comment;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "images", columnDefinition = "TEXT[]")
    private List<String> images; // 이미지 URL 배열

    // Constructors
    protected PoolReview() {
    }

    public PoolReview(SwimmingPool pool, User user, BigDecimal rating, String comment) {
        this.pool = pool;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public SwimmingPool getPool() {
        return pool;
    }

    public void setPool(SwimmingPool pool) {
        this.pool = pool;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}

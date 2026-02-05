package com.zalmuk.swwim.api.entity.pool;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * 저장한 수영장 (즐겨찾기)
 */
@Entity
@Table(name = "saved_pools")
public class SavedPool extends BaseTimeEntity {

    @EmbeddedId
    private SavedPoolId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("poolId")
    @JoinColumn(name = "pool_id")
    private SwimmingPool pool;

    // Constructors
    protected SavedPool() {
    }

    public SavedPool(User user, SwimmingPool pool) {
        this.user = user;
        this.pool = pool;
        this.id = new SavedPoolId(user.getId(), pool.getId());
    }

    // Getters
    public SavedPoolId getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public SwimmingPool getPool() {
        return pool;
    }

    // Embedded ID Class
    @Embeddable
    public static class SavedPoolId implements Serializable {
        @Column(name = "user_id", length = 128)
        private String userId;

        @Column(name = "pool_id")
        private Integer poolId;

        protected SavedPoolId() {
        }

        public SavedPoolId(String userId, Integer poolId) {
            this.userId = userId;
            this.poolId = poolId;
        }

        public String getUserId() {
            return userId;
        }

        public Integer getPoolId() {
            return poolId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SavedPoolId that = (SavedPoolId) o;
            return Objects.equals(userId, that.userId) &&
                    Objects.equals(poolId, that.poolId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, poolId);
        }
    }
}

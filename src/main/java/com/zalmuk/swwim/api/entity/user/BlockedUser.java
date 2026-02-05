package com.zalmuk.swwim.api.entity.user;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * 차단한 사용자 (다대다)
 */
@Entity
@Table(name = "blocked_users")
public class BlockedUser extends BaseTimeEntity {

    @EmbeddedId
    private BlockedUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("blockedUserId")
    @JoinColumn(name = "blocked_user_id")
    private User blockedUser;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    // Constructors
    protected BlockedUser() {
    }

    public BlockedUser(User user, User blockedUser) {
        this.user = user;
        this.blockedUser = blockedUser;
        this.id = new BlockedUserId(user.getId(), blockedUser.getId());
    }

    // Getters and Setters
    public BlockedUserId getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public User getBlockedUser() {
        return blockedUser;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    // Embedded ID Class
    @Embeddable
    public static class BlockedUserId implements Serializable {
        @Column(name = "user_id", length = 128)
        private String userId;

        @Column(name = "blocked_user_id", length = 128)
        private String blockedUserId;

        protected BlockedUserId() {
        }

        public BlockedUserId(String userId, String blockedUserId) {
            this.userId = userId;
            this.blockedUserId = blockedUserId;
        }

        public String getUserId() {
            return userId;
        }

        public String getBlockedUserId() {
            return blockedUserId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlockedUserId that = (BlockedUserId) o;
            return Objects.equals(userId, that.userId) &&
                    Objects.equals(blockedUserId, that.blockedUserId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, blockedUserId);
        }
    }
}

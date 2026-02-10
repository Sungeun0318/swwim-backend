package com.zalmuk.swwim.api.entity.user;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * 팔로우 관계 (다대다)
 */
@Entity
@Table(name = "user_follows", indexes = {
        @Index(name = "idx_user_follows_follower", columnList = "follower_id"),
        @Index(name = "idx_user_follows_following", columnList = "following_id")
})
public class UserFollow extends BaseTimeEntity {

    @EmbeddedId
    private UserFollowId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followingId")
    @JoinColumn(name = "following_id")
    private User following;

    // Constructors
    protected UserFollow() {
    }

    public UserFollow(User follower, User following) {
        this.follower = follower;
        this.following = following;
        this.id = new UserFollowId(follower.getId(), following.getId());
    }

    // Getters
    public UserFollowId getId() {
        return id;
    }

    public User getFollower() {
        return follower;
    }

    public User getFollowing() {
        return following;
    }

    // Embedded ID Class
    @Embeddable
    public static class UserFollowId implements Serializable {
        @Column(name = "follower_id", length = 128)
        private String followerId;

        @Column(name = "following_id", length = 128)
        private String followingId;

        protected UserFollowId() {
        }

        public UserFollowId(String followerId, String followingId) {
            this.followerId = followerId;
            this.followingId = followingId;
        }

        public String getFollowerId() {
            return followerId;
        }

        public String getFollowingId() {
            return followingId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserFollowId that = (UserFollowId) o;
            return Objects.equals(followerId, that.followerId) &&
                    Objects.equals(followingId, that.followingId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(followerId, followingId);
        }
    }
}

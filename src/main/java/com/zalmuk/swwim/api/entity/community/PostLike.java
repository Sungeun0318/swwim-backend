package com.zalmuk.swwim.api.entity.community;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * 게시글 좋아요 (다대다)
 */
@Entity
@Table(name = "post_likes", indexes = {
        @Index(name = "idx_post_likes_post", columnList = "post_id"),
        @Index(name = "idx_post_likes_user", columnList = "user_id")
})
public class PostLike extends BaseTimeEntity {

    @EmbeddedId
    private PostLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private CommunityPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors
    protected PostLike() {
    }

    public PostLike(CommunityPost post, User user) {
        this.post = post;
        this.user = user;
        this.id = new PostLikeId(post.getId(), user.getId());
    }

    // Getters
    public PostLikeId getId() {
        return id;
    }

    public CommunityPost getPost() {
        return post;
    }

    public User getUser() {
        return user;
    }

    // Embedded ID Class
    @Embeddable
    public static class PostLikeId implements Serializable {
        @Column(name = "post_id")
        private UUID postId;

        @Column(name = "user_id", length = 128)
        private String userId;

        protected PostLikeId() {
        }

        public PostLikeId(UUID postId, String userId) {
            this.postId = postId;
            this.userId = userId;
        }

        public UUID getPostId() {
            return postId;
        }

        public String getUserId() {
            return userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PostLikeId that = (PostLikeId) o;
            return Objects.equals(postId, that.postId) &&
                    Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(postId, userId);
        }
    }
}

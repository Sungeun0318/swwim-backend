package com.zalmuk.swwim.api.entity.community;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * 게시글 저장 (북마크)
 */
@Entity
@Table(name = "saved_posts", indexes = {
        @Index(name = "idx_saved_posts_user", columnList = "user_id"),
        @Index(name = "idx_saved_posts_post", columnList = "post_id")
})
public class SavedPost extends BaseTimeEntity {

    @EmbeddedId
    private SavedPostId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private CommunityPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    protected SavedPost() {
    }

    public SavedPost(CommunityPost post, User user) {
        this.post = post;
        this.user = user;
        this.id = new SavedPostId(post.getId(), user.getId());
    }

    public SavedPostId getId() {
        return id;
    }

    public CommunityPost getPost() {
        return post;
    }

    public User getUser() {
        return user;
    }

    @Embeddable
    public static class SavedPostId implements Serializable {
        @Column(name = "post_id")
        private UUID postId;

        @Column(name = "user_id", length = 128)
        private String userId;

        protected SavedPostId() {
        }

        public SavedPostId(UUID postId, String userId) {
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
            SavedPostId that = (SavedPostId) o;
            return Objects.equals(postId, that.postId) &&
                    Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(postId, userId);
        }
    }
}

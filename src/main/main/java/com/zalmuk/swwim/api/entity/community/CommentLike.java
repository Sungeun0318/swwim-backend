package com.zalmuk.swwim.api.entity.community;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * 댓글 좋아요 (다대다)
 */
@Entity
@Table(name = "comment_likes")
public class CommentLike extends BaseTimeEntity {

    @EmbeddedId
    private CommentLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    private PostComment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors
    protected CommentLike() {
    }

    public CommentLike(PostComment comment, User user) {
        this.comment = comment;
        this.user = user;
        this.id = new CommentLikeId(comment.getId(), user.getId());
    }

    // Getters
    public CommentLikeId getId() {
        return id;
    }

    public PostComment getComment() {
        return comment;
    }

    public User getUser() {
        return user;
    }

    // Embedded ID Class
    @Embeddable
    public static class CommentLikeId implements Serializable {
        @Column(name = "comment_id")
        private UUID commentId;

        @Column(name = "user_id", length = 128)
        private String userId;

        protected CommentLikeId() {
        }

        public CommentLikeId(UUID commentId, String userId) {
            this.commentId = commentId;
            this.userId = userId;
        }

        public UUID getCommentId() {
            return commentId;
        }

        public String getUserId() {
            return userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CommentLikeId that = (CommentLikeId) o;
            return Objects.equals(commentId, that.commentId) &&
                    Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(commentId, userId);
        }
    }
}

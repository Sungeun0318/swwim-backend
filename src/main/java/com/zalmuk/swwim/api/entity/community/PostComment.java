package com.zalmuk.swwim.api.entity.community;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 게시글 댓글
 */
@Entity
@Table(name = "post_comments", indexes = {
        @Index(name = "idx_post_comments_post", columnList = "post_id"),
        @Index(name = "idx_post_comments_user", columnList = "user_id")
})
public class PostComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    // 통계
    @Column(name = "like_count")
    private Integer likeCount = 0;

    // 연관관계
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentLike> likes = new HashSet<>();

    // Constructors
    protected PostComment() {
    }

    public PostComment(CommunityPost post, User user, String text) {
        this.post = post;
        this.user = user;
        this.text = text;
    }

    // Helper methods
    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public CommunityPost getPost() {
        return post;
    }

    public void setPost(CommunityPost post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Set<CommentLike> getLikes() {
        return likes;
    }
}

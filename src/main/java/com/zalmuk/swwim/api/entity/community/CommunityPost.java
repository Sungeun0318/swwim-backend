package com.zalmuk.swwim.api.entity.community;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 커뮤니티 게시글
 */
@Entity
@Table(name = "community_posts", indexes = {
        @Index(name = "idx_community_posts_user", columnList = "user_id"),
        @Index(name = "idx_community_posts_created", columnList = "created_at DESC")
})
public class CommunityPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    // 통계
    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Column(name = "share_count")
    private Integer shareCount = 0;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    // 공유 URL
    @Column(name = "share_url", columnDefinition = "TEXT")
    private String shareUrl;

    // 연관관계
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private Set<PostComment> comments = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostLike> likes = new HashSet<>();

    // Constructors
    protected CommunityPost() {
    }

    public CommunityPost(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
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

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public Set<PostComment> getComments() {
        return comments;
    }

    public Set<PostLike> getLikes() {
        return likes;
    }
}

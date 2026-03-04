package com.zalmuk.swwim.api.service.community;

import com.zalmuk.swwim.api.entity.community.*;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.repository.community.*;
import com.zalmuk.swwim.api.repository.user.BlockedUserRepository;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import com.zalmuk.swwim.api.service.notification.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CommunityService {

    private final CommunityPostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final BlockedUserRepository blockedUserRepository;
    private final NotificationService notificationService;

    public CommunityService(CommunityPostRepository postRepository,
                            PostCommentRepository commentRepository,
                            PostLikeRepository postLikeRepository,
                            CommentLikeRepository commentLikeRepository,
                            UserRepository userRepository,
                            BlockedUserRepository blockedUserRepository,
                            NotificationService notificationService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.userRepository = userRepository;
        this.blockedUserRepository = blockedUserRepository;
        this.notificationService = notificationService;
    }

    // 게시글 조회
    public Optional<CommunityPost> findPostById(UUID id) {
        return postRepository.findByIdWithUser(id);
    }

    public Page<CommunityPost> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<CommunityPost> getPostsExcludingBlocked(String userId, Pageable pageable) {
        List<String> blockedIds = blockedUserRepository.findBlockedUserIdsByUserId(userId);
        if (blockedIds.isEmpty()) {
            return postRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        return postRepository.findAllExcludingBlockedUsers(blockedIds, pageable);
    }

    public Page<CommunityPost> getUserPosts(String userId, Pageable pageable) {
        return userRepository.findById(userId)
            .map(user -> postRepository.findByUserOrderByCreatedAtDesc(user, pageable))
            .orElse(Page.empty());
    }

    public Page<CommunityPost> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchByKeyword(keyword, pageable);
    }

    public Page<CommunityPost> getPopularPosts(Pageable pageable) {
        return postRepository.findTopByLikeCount(pageable);
    }

    // 게시글 작성/수정/삭제
    @Transactional
    public CommunityPost createPost(String userId, String title, String content, String imageUrl) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        CommunityPost post = new CommunityPost(user, title, content);
        post.setImageUrl(imageUrl);
        post = postRepository.save(post);

        // 사용자 게시글 카운트 증가
        user.setPostCount(user.getPostCount() + 1);
        userRepository.save(user);

        return post;
    }

    @Transactional
    public CommunityPost updatePost(UUID postId, String userId, String title, String content, String imageUrl) {
        CommunityPost post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }

        post.setTitle(title);
        post.setContent(content);
        post.setImageUrl(imageUrl);
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(UUID postId, String userId) {
        CommunityPost post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        }

        User user = post.getUser();
        postRepository.delete(post);

        user.setPostCount(Math.max(0, user.getPostCount() - 1));
        userRepository.save(user);
    }

    @Transactional
    public void incrementViewCount(UUID postId) {
        postRepository.incrementViewCount(postId);
    }

    // 좋아요
    public boolean isPostLiked(UUID postId, String userId) {
        return postRepository.findById(postId)
            .flatMap(post -> userRepository.findById(userId)
                .map(user -> postLikeRepository.existsByPostAndUser(post, user)))
            .orElse(false);
    }

    /// 여러 댓글에 대해 사용자가 좋아요한 ID 목록 배치 조회 (N+1 방지)
    public Set<UUID> getLikedCommentIds(String userId, List<UUID> commentIds) {
        if (userId == null || commentIds == null || commentIds.isEmpty()) return Set.of();
        return commentLikeRepository.findLikedCommentIds(userId, commentIds);
    }

    /// 여러 게시글에 대해 사용자가 좋아요한 ID 목록 배치 조회 (N+1 방지)
    public Set<UUID> getLikedPostIds(String userId, List<UUID> postIds) {
        if (userId == null || postIds == null || postIds.isEmpty()) return Set.of();
        return postLikeRepository.findLikedPostIds(userId, postIds);
    }

    /// 여러 게시글의 실제 좋아요 수 배치 조회 (PostLike 테이블 기준)
    public Map<UUID, Integer> getLikeCounts(List<UUID> postIds) {
        if (postIds == null || postIds.isEmpty()) return Map.of();
        List<Object[]> rows = postLikeRepository.countLikesByPostIds(postIds);
        Map<UUID, Integer> result = new HashMap<>();
        for (Object[] row : rows) {
            UUID postId = (UUID) row[0];
            long count = (Long) row[1];
            result.put(postId, (int) count);
        }
        return result;
    }

    @Transactional
    public void likePost(UUID postId, String userId) {
        // ID로 직접 조회 (엔티티 비교 캐싱 문제 방지)
        boolean alreadyLiked = postLikeRepository.findLikedPostIds(userId, List.of(postId)).contains(postId);
        if (alreadyLiked) return; // 이미 좋아요한 경우 무시

        CommunityPost post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        PostLike like = new PostLike(post, user);
        postLikeRepository.save(like);
        // 원자적 INCREMENT (race condition 방지)
        postRepository.incrementLikeCount(postId);

        // 좋아요 알림 생성 (실패해도 좋아요 트랜잭션에 영향 없도록 try-catch)
        try {
            notificationService.sendLikeNotification(userId, post.getUser().getId(), postId.toString());
        } catch (Exception ignored) {}
    }

    @Transactional
    public void unlikePost(UUID postId, String userId) {
        // ID로 직접 존재 여부 확인
        boolean liked = postLikeRepository.findLikedPostIds(userId, List.of(postId)).contains(postId);
        if (!liked) return; // 좋아요 없으면 무시

        CommunityPost post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        postLikeRepository.findByPostAndUser(post, user).ifPresent(like -> {
            postLikeRepository.delete(like);
            // 원자적 DECREMENT (0 미만 방지)
            postRepository.decrementLikeCount(postId);
        });
    }

    // 댓글
    public Page<PostComment> getComments(UUID postId, Pageable pageable) {
        return postRepository.findById(postId)
            .map(post -> commentRepository.findByPostOrderByCreatedAtDesc(post, pageable))
            .orElse(Page.empty());
    }

    @Transactional
    public PostComment createComment(UUID postId, String userId, String text) {
        CommunityPost post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        PostComment comment = new PostComment(post, user, text);
        comment = commentRepository.save(comment);

        post.incrementCommentCount();
        postRepository.save(post);

        // 댓글 알림 생성 (실패해도 댓글 트랜잭션에 영향 없도록 try-catch)
        try {
            notificationService.sendCommentNotification(userId, post.getUser().getId(), postId.toString());
        } catch (Exception ignored) {}

        return comment;
    }

    @Transactional
    public void deleteComment(UUID commentId, String userId) {
        PostComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 댓글만 삭제할 수 있습니다.");
        }

        CommunityPost post = comment.getPost();
        commentRepository.delete(comment);

        post.decrementCommentCount();
        postRepository.save(post);
    }

    // 댓글 좋아요
    @Transactional
    public void likeComment(UUID commentId, String userId) {
        PostComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!commentLikeRepository.existsByCommentAndUser(comment, user)) {
            CommentLike like = new CommentLike(comment, user);
            commentLikeRepository.save(like);
            comment.incrementLikeCount();
            commentRepository.save(comment);
        }
    }

    @Transactional
    public void unlikeComment(UUID commentId, String userId) {
        PostComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        commentLikeRepository.findByCommentAndUser(comment, user).ifPresent(like -> {
            commentLikeRepository.delete(like);
            comment.decrementLikeCount();
            commentRepository.save(comment);
        });
    }
}

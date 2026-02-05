package com.zalmuk.swwim.api.repository.community;

import com.zalmuk.swwim.api.entity.community.CommunityPost;
import com.zalmuk.swwim.api.entity.community.PostComment;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, UUID> {

    Page<PostComment> findByPostOrderByCreatedAtDesc(CommunityPost post, Pageable pageable);

    Page<PostComment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.user.id NOT IN :blockedUserIds ORDER BY pc.createdAt DESC")
    Page<PostComment> findByPostExcludingBlockedUsers(
            @Param("postId") UUID postId,
            @Param("blockedUserIds") List<String> blockedUserIds,
            Pageable pageable);

    long countByPostId(UUID postId);

    void deleteByPost(CommunityPost post);
}

package com.zalmuk.swwim.api.repository.community;

import com.zalmuk.swwim.api.entity.community.CommunityPost;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, UUID> {

    @Query("SELECT cp FROM CommunityPost cp JOIN FETCH cp.user ORDER BY cp.createdAt DESC")
    Page<CommunityPost> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT cp FROM CommunityPost cp JOIN FETCH cp.user WHERE cp.user = :user ORDER BY cp.createdAt DESC")
    Page<CommunityPost> findByUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);

    @Query("SELECT cp FROM CommunityPost cp JOIN FETCH cp.user WHERE LOWER(cp.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(cp.content) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY cp.createdAt DESC")
    Page<CommunityPost> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT cp FROM CommunityPost cp JOIN FETCH cp.user WHERE cp.user.id NOT IN :blockedUserIds ORDER BY cp.createdAt DESC")
    Page<CommunityPost> findAllExcludingBlockedUsers(@Param("blockedUserIds") List<String> blockedUserIds, Pageable pageable);

    @Query("SELECT cp FROM CommunityPost cp JOIN FETCH cp.user ORDER BY cp.likeCount DESC")
    Page<CommunityPost> findTopByLikeCount(Pageable pageable);

    @Modifying
    @Query("UPDATE CommunityPost cp SET cp.viewCount = cp.viewCount + 1 WHERE cp.id = :postId")
    void incrementViewCount(@Param("postId") UUID postId);

    long countByUserId(String userId);
}

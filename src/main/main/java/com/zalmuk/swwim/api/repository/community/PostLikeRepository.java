package com.zalmuk.swwim.api.repository.community;

import com.zalmuk.swwim.api.entity.community.CommunityPost;
import com.zalmuk.swwim.api.entity.community.PostLike;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLike.PostLikeId> {

    boolean existsByPostAndUser(CommunityPost post, User user);

    Optional<PostLike> findByPostAndUser(CommunityPost post, User user);

    void deleteByPostAndUser(CommunityPost post, User user);

    long countByPostId(UUID postId);

    @Query("SELECT pl.user FROM PostLike pl WHERE pl.post.id = :postId")
    List<User> findUsersByPostId(@Param("postId") UUID postId);

    void deleteByPost(CommunityPost post);

    /// 특정 사용자가 좋아요한 게시글 ID 목록 (배치 조회)
    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.user.id = :userId AND pl.post.id IN :postIds")
    Set<UUID> findLikedPostIds(@Param("userId") String userId, @Param("postIds") List<UUID> postIds);

    /// 여러 게시글의 실제 좋아요 수 배치 조회 (Object[]{postId, count})
    @Query("SELECT pl.post.id, COUNT(pl) FROM PostLike pl WHERE pl.post.id IN :postIds GROUP BY pl.post.id")
    List<Object[]> countLikesByPostIds(@Param("postIds") List<UUID> postIds);
}

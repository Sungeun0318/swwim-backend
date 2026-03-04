package com.zalmuk.swwim.api.repository.community;

import com.zalmuk.swwim.api.entity.community.CommentLike;
import com.zalmuk.swwim.api.entity.community.PostComment;
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
public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLike.CommentLikeId> {

    boolean existsByCommentAndUser(PostComment comment, User user);

    Optional<CommentLike> findByCommentAndUser(PostComment comment, User user);

    void deleteByCommentAndUser(PostComment comment, User user);

    long countByCommentId(UUID commentId);

    void deleteByComment(PostComment comment);

    /// 특정 사용자가 좋아요한 댓글 ID 목록 (배치 조회)
    @Query("SELECT cl.comment.id FROM CommentLike cl WHERE cl.user.id = :userId AND cl.comment.id IN :commentIds")
    Set<UUID> findLikedCommentIds(@Param("userId") String userId, @Param("commentIds") List<UUID> commentIds);
}

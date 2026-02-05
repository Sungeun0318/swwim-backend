package com.zalmuk.swwim.api.repository.community;

import com.zalmuk.swwim.api.entity.community.CommentLike;
import com.zalmuk.swwim.api.entity.community.PostComment;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLike.CommentLikeId> {

    boolean existsByCommentAndUser(PostComment comment, User user);

    Optional<CommentLike> findByCommentAndUser(PostComment comment, User user);

    void deleteByCommentAndUser(PostComment comment, User user);

    long countByCommentId(UUID commentId);

    void deleteByComment(PostComment comment);
}

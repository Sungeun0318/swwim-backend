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

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLike.PostLikeId> {

    boolean existsByPostAndUser(CommunityPost post, User user);

    Optional<PostLike> findByPostAndUser(CommunityPost post, User user);

    void deleteByPostAndUser(CommunityPost post, User user);

    long countByPostId(java.util.UUID postId);

    @Query("SELECT pl.user FROM PostLike pl WHERE pl.post.id = :postId")
    List<User> findUsersByPostId(@Param("postId") java.util.UUID postId);

    void deleteByPost(CommunityPost post);
}

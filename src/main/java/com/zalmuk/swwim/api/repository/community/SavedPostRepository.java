package com.zalmuk.swwim.api.repository.community;

import com.zalmuk.swwim.api.entity.community.CommunityPost;
import com.zalmuk.swwim.api.entity.community.SavedPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface SavedPostRepository extends JpaRepository<SavedPost, SavedPost.SavedPostId> {

    boolean existsById(SavedPost.SavedPostId id);

    @Query(value = "SELECT sp.post FROM SavedPost sp JOIN FETCH sp.post.user WHERE sp.user.id = :userId ORDER BY sp.createdAt DESC",
           countQuery = "SELECT COUNT(sp) FROM SavedPost sp WHERE sp.user.id = :userId")
    Page<CommunityPost> findSavedPostsByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT sp.post.id FROM SavedPost sp WHERE sp.user.id = :userId AND sp.post.id IN :postIds")
    Set<UUID> findSavedPostIdsByUserIdAndPostIds(@Param("userId") String userId, @Param("postIds") List<UUID> postIds);
}

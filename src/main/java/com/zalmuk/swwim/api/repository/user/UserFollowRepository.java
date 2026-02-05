package com.zalmuk.swwim.api.repository.user;

import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.entity.user.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, UserFollow.UserFollowId> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<UserFollow> findByFollowerAndFollowing(User follower, User following);

    @Query("SELECT uf.following FROM UserFollow uf WHERE uf.follower.id = :userId")
    Page<User> findFollowingByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT uf.follower FROM UserFollow uf WHERE uf.following.id = :userId")
    Page<User> findFollowersByUserId(@Param("userId") String userId, Pageable pageable);

    long countByFollowerId(String followerId);

    long countByFollowingId(String followingId);

    void deleteByFollowerAndFollowing(User follower, User following);
}

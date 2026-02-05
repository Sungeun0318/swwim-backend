package com.zalmuk.swwim.api.repository.user;

import com.zalmuk.swwim.api.entity.user.BlockedUser;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUser, BlockedUser.BlockedUserId> {

    boolean existsByUserAndBlockedUser(User user, User blockedUser);

    Optional<BlockedUser> findByUserAndBlockedUser(User user, User blockedUser);

    @Query("SELECT bu.blockedUser FROM BlockedUser bu WHERE bu.user.id = :userId")
    List<User> findBlockedUsersByUserId(@Param("userId") String userId);

    @Query("SELECT bu.blockedUser.id FROM BlockedUser bu WHERE bu.user.id = :userId")
    List<String> findBlockedUserIdsByUserId(@Param("userId") String userId);

    void deleteByUserAndBlockedUser(User user, User blockedUser);
}

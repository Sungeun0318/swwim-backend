package com.zalmuk.swwim.api.repository.user;

import com.zalmuk.swwim.api.entity.enums.UserStatus;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByNickname(String nickname);

    boolean existsByNickname(String nickname);

    List<User> findByStatus(UserStatus status);

    @Query("SELECT u FROM User u WHERE u.isPremium = true")
    List<User> findPremiumUsers();

    @Query("SELECT u FROM User u WHERE u.isAdmin = true")
    List<User> findAdminUsers();

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByNameOrNickname(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE u.provider = :provider AND u.providerId = :providerId")
    Optional<User> findByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);
}

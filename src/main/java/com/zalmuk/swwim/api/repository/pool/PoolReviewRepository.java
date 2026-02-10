package com.zalmuk.swwim.api.repository.pool;

import com.zalmuk.swwim.api.entity.pool.PoolReview;
import com.zalmuk.swwim.api.entity.pool.SwimmingPool;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PoolReviewRepository extends JpaRepository<PoolReview, UUID> {

    Page<PoolReview> findByPoolOrderByCreatedAtDesc(SwimmingPool pool, Pageable pageable);

    Page<PoolReview> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Optional<PoolReview> findByPoolAndUser(SwimmingPool pool, User user);

    boolean existsByPoolAndUser(SwimmingPool pool, User user);

    @Query("SELECT AVG(pr.rating) FROM PoolReview pr WHERE pr.pool.id = :poolId")
    BigDecimal calculateAverageRating(@Param("poolId") Integer poolId);

    @Query("SELECT COUNT(pr) FROM PoolReview pr WHERE pr.pool.id = :poolId")
    long countByPoolId(@Param("poolId") Integer poolId);
}

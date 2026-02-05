package com.zalmuk.swwim.api.repository.pool;

import com.zalmuk.swwim.api.entity.pool.SavedPool;
import com.zalmuk.swwim.api.entity.pool.SwimmingPool;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavedPoolRepository extends JpaRepository<SavedPool, SavedPool.SavedPoolId> {

    boolean existsByUserAndPool(User user, SwimmingPool pool);

    Optional<SavedPool> findByUserAndPool(User user, SwimmingPool pool);

    @Query("SELECT sp.pool FROM SavedPool sp WHERE sp.user.id = :userId ORDER BY sp.createdAt DESC")
    Page<SwimmingPool> findSavedPoolsByUserId(@Param("userId") String userId, Pageable pageable);

    void deleteByUserAndPool(User user, SwimmingPool pool);

    long countByUserId(String userId);
}

package com.zalmuk.swwim.api.repository.quickstart;

import com.zalmuk.swwim.api.entity.enums.UserLevel;
import com.zalmuk.swwim.api.entity.quickstart.QuickstartProgress;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuickstartProgressRepository extends JpaRepository<QuickstartProgress, UUID> {

    Optional<QuickstartProgress> findByUserAndLevel(User user, UserLevel level);

    List<QuickstartProgress> findByUserOrderByLevelAsc(User user);

    boolean existsByUserAndLevel(User user, UserLevel level);
}

package com.zalmuk.swwim.api.repository.quickstart;

import com.zalmuk.swwim.api.entity.quickstart.QuickstartDailyCompletion;
import com.zalmuk.swwim.api.entity.quickstart.QuickstartWeekProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuickstartDailyCompletionRepository extends JpaRepository<QuickstartDailyCompletion, UUID> {

    List<QuickstartDailyCompletion> findByWeekProgressOrderByCompletionDateAsc(QuickstartWeekProgress weekProgress);

    Optional<QuickstartDailyCompletion> findByWeekProgressAndCompletionDate(QuickstartWeekProgress weekProgress, LocalDate completionDate);

    boolean existsByWeekProgressAndCompletionDate(QuickstartWeekProgress weekProgress, LocalDate completionDate);

    long countByWeekProgress(QuickstartWeekProgress weekProgress);
}

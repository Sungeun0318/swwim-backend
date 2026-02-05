package com.zalmuk.swwim.api.repository.quickstart;

import com.zalmuk.swwim.api.entity.quickstart.QuickstartProgress;
import com.zalmuk.swwim.api.entity.quickstart.QuickstartWeekProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuickstartWeekProgressRepository extends JpaRepository<QuickstartWeekProgress, UUID> {

    List<QuickstartWeekProgress> findByProgressOrderByWeekAsc(QuickstartProgress progress);

    Optional<QuickstartWeekProgress> findByProgressAndWeek(QuickstartProgress progress, Integer week);

    boolean existsByProgressAndWeek(QuickstartProgress progress, Integer week);
}

package com.zalmuk.swwim.api.repository.training;

import com.zalmuk.swwim.api.entity.training.TrainingDetail;
import com.zalmuk.swwim.api.entity.training.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingDetailRepository extends JpaRepository<TrainingDetail, UUID> {

    List<TrainingDetail> findBySessionOrderByOrderIndexAsc(TrainingSession session);

    void deleteBySession(TrainingSession session);
}

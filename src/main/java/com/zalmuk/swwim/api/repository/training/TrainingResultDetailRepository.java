package com.zalmuk.swwim.api.repository.training;

import com.zalmuk.swwim.api.entity.training.TrainingResult;
import com.zalmuk.swwim.api.entity.training.TrainingResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingResultDetailRepository extends JpaRepository<TrainingResultDetail, UUID> {

    List<TrainingResultDetail> findByResultOrderByOrderIndexAsc(TrainingResult result);

    void deleteByResult(TrainingResult result);
}

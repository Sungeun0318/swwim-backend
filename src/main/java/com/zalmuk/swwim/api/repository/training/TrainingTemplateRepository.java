package com.zalmuk.swwim.api.repository.training;

import com.zalmuk.swwim.api.entity.training.TrainingTemplate;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrainingTemplateRepository extends JpaRepository<TrainingTemplate, UUID> {

    Page<TrainingTemplate> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Query("SELECT tt FROM TrainingTemplate tt WHERE tt.isPublic = true ORDER BY tt.useCount DESC")
    Page<TrainingTemplate> findPublicTemplates(Pageable pageable);

    @Query("SELECT tt FROM TrainingTemplate tt WHERE tt.isPublic = true AND LOWER(tt.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<TrainingTemplate> searchPublicTemplates(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT tt FROM TrainingTemplate tt WHERE tt.user.id = :userId OR tt.isPublic = true ORDER BY tt.createdAt DESC")
    Page<TrainingTemplate> findAvailableTemplates(@Param("userId") String userId, Pageable pageable);

    long countByUserId(String userId);
}

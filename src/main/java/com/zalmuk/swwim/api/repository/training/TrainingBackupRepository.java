package com.zalmuk.swwim.api.repository.training;

import com.zalmuk.swwim.api.entity.enums.BackupType;
import com.zalmuk.swwim.api.entity.training.TrainingBackup;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainingBackupRepository extends JpaRepository<TrainingBackup, UUID> {

    Page<TrainingBackup> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Query("SELECT tb FROM TrainingBackup tb WHERE tb.user.id = :userId ORDER BY tb.createdAt DESC LIMIT 1")
    Optional<TrainingBackup> findLatestByUserId(@Param("userId") String userId);

    Page<TrainingBackup> findByUserAndBackupTypeOrderByCreatedAtDesc(User user, BackupType backupType, Pageable pageable);

    void deleteByUser(User user);
}

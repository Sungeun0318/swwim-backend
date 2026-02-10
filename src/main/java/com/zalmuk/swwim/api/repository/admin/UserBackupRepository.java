package com.zalmuk.swwim.api.repository.admin;

import com.zalmuk.swwim.api.entity.admin.UserBackup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserBackupRepository extends JpaRepository<UserBackup, UUID> {

    List<UserBackup> findByUserIdOrderByCreatedAtDesc(String userId);

    Page<UserBackup> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

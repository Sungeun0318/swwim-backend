package com.zalmuk.swwim.api.repository.admin;

import com.zalmuk.swwim.api.entity.admin.DeletionRequest;
import com.zalmuk.swwim.api.entity.enums.DeletionStatus;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeletionRequestRepository extends JpaRepository<DeletionRequest, UUID> {

    Optional<DeletionRequest> findByUser(User user);

    Optional<DeletionRequest> findByUserAndStatus(User user, DeletionStatus status);

    @Query("SELECT dr FROM DeletionRequest dr WHERE dr.status = :status AND dr.scheduledDeletionAt <= :now")
    List<DeletionRequest> findDueForDeletion(@Param("status") DeletionStatus status, @Param("now") LocalDateTime now);

    boolean existsByUserAndStatus(User user, DeletionStatus status);

    void deleteByUser(User user);
}

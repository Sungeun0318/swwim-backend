package com.zalmuk.swwim.api.entity.admin;

import com.zalmuk.swwim.api.entity.enums.DeletionStatus;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 회원 탈퇴 요청
 */
@Entity
@Table(name = "deletion_requests",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}))
public class DeletionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private DeletionStatus status = DeletionStatus.PENDING;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "scheduled_deletion_at", nullable = false)
    private LocalDateTime scheduledDeletionAt; // 30일 후

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Constructors
    protected DeletionRequest() {
    }

    public DeletionRequest(User user) {
        this.user = user;
        this.requestedAt = LocalDateTime.now();
        this.scheduledDeletionAt = LocalDateTime.now().plusDays(30);
    }

    // Helper methods
    public void cancel() {
        this.status = DeletionStatus.CANCELLED;
    }

    public void complete() {
        this.status = DeletionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isDueForDeletion() {
        return status == DeletionStatus.PENDING &&
                LocalDateTime.now().isAfter(scheduledDeletionAt);
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DeletionStatus getStatus() {
        return status;
    }

    public void setStatus(DeletionStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getScheduledDeletionAt() {
        return scheduledDeletionAt;
    }

    public void setScheduledDeletionAt(LocalDateTime scheduledDeletionAt) {
        this.scheduledDeletionAt = scheduledDeletionAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}

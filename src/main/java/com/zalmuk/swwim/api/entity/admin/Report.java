package com.zalmuk.swwim.api.entity.admin;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import com.zalmuk.swwim.api.entity.enums.ReportStatus;
import com.zalmuk.swwim.api.entity.enums.ReportType;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 신고
 */
@Entity
@Table(name = "reports", indexes = {
        @Index(name = "idx_reports_status", columnList = "status"),
        @Index(name = "idx_reports_created", columnList = "created_at DESC")
})
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 20)
    private ReportType reportType;

    @Column(name = "target_id", nullable = false)
    private String targetId;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    // 처리 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private User resolvedBy;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    // Constructors
    protected Report() {
    }

    public Report(User reporter, ReportType reportType, String targetId, String reason) {
        this.reporter = reporter;
        this.reportType = reportType;
        this.targetId = targetId;
        this.reason = reason;
    }

    // Helper methods
    public void resolve(User admin, ReportStatus status, String adminNote) {
        this.resolvedBy = admin;
        this.status = status;
        this.adminNote = adminNote;
        this.resolvedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public User getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(User resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}

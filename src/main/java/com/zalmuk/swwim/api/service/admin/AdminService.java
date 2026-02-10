package com.zalmuk.swwim.api.service.admin;

import com.zalmuk.swwim.api.entity.admin.*;
import com.zalmuk.swwim.api.entity.enums.*;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.repository.admin.*;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private final ReportRepository reportRepository;
    private final UserSanctionRepository sanctionRepository;
    private final DeletionRequestRepository deletionRequestRepository;
    private final UserBackupRepository userBackupRepository;
    private final UserRepository userRepository;

    public AdminService(ReportRepository reportRepository,
                        UserSanctionRepository sanctionRepository,
                        DeletionRequestRepository deletionRequestRepository,
                        UserBackupRepository userBackupRepository,
                        UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.sanctionRepository = sanctionRepository;
        this.deletionRequestRepository = deletionRequestRepository;
        this.userBackupRepository = userBackupRepository;
        this.userRepository = userRepository;
    }

    // 신고 관련
    public Page<Report> getAllReports(Pageable pageable) {
        return reportRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<Report> getReportsByStatus(ReportStatus status, Pageable pageable) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    public long getPendingReportCount() {
        return reportRepository.countByStatus(ReportStatus.PENDING);
    }

    @Transactional
    public Report createReport(String reporterId, ReportType type, String targetId, String reason) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("신고자를 찾을 수 없습니다."));

        if (reportRepository.existsByReporterIdAndTargetId(reporterId, targetId)) {
            throw new IllegalStateException("이미 신고한 대상입니다.");
        }

        Report report = new Report(reporter, type, targetId, reason);
        return reportRepository.save(report);
    }

    @Transactional
    public Report resolveReport(UUID reportId, String adminId, ReportStatus status, String adminNote) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다."));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        if (!admin.getIsAdmin()) {
            throw new IllegalArgumentException("관리자 권한이 필요합니다.");
        }

        report.resolve(admin, status, adminNote);
        return reportRepository.save(report);
    }

    // 제재 관련
    public Page<UserSanction> getActiveSanctions(Pageable pageable) {
        return sanctionRepository.findAllActive(pageable);
    }

    public List<UserSanction> getUserActiveSanctions(String userId) {
        return sanctionRepository.findActiveByUserId(userId);
    }

    public boolean isUserSanctioned(String userId) {
        return sanctionRepository.existsByUserIdAndIsActiveTrue(userId);
    }

    @Transactional
    public UserSanction createSanction(String userId, String adminId, SanctionType type,
                                        String reason, LocalDateTime endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        if (!admin.getIsAdmin()) {
            throw new IllegalArgumentException("관리자 권한이 필요합니다.");
        }

        UserSanction sanction = new UserSanction(user, admin, type, reason);
        sanction.setEndDate(endDate);
        sanction = sanctionRepository.save(sanction);

        // 영구 정지인 경우 사용자 상태 변경
        if (type == SanctionType.BAN) {
            user.setStatus(UserStatus.SUSPENDED);
            userRepository.save(user);
        }

        return sanction;
    }

    @Transactional
    public void deactivateSanction(UUID sanctionId, String adminId) {
        UserSanction sanction = sanctionRepository.findById(sanctionId)
                .orElseThrow(() -> new IllegalArgumentException("제재를 찾을 수 없습니다."));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        if (!admin.getIsAdmin()) {
            throw new IllegalArgumentException("관리자 권한이 필요합니다.");
        }

        sanction.deactivate();
        sanctionRepository.save(sanction);

        // 활성 제재가 없으면 사용자 상태 복원
        if (sanctionRepository.findActiveByUserId(sanction.getUser().getId()).isEmpty()) {
            User user = sanction.getUser();
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }
    }

    @Transactional
    public void processExpiredSanctions() {
        List<UserSanction> expiredSanctions = sanctionRepository.findExpiredSanctions();
        for (UserSanction sanction : expiredSanctions) {
            sanction.deactivate();
            sanctionRepository.save(sanction);
        }
    }

    // 탈퇴 요청 관련
    public Optional<DeletionRequest> getUserDeletionRequest(String userId) {
        return userRepository.findById(userId)
                .flatMap(deletionRequestRepository::findByUser);
    }

    @Transactional
    public DeletionRequest requestDeletion(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (deletionRequestRepository.existsByUserAndStatus(user, DeletionStatus.PENDING)) {
            throw new IllegalStateException("이미 탈퇴 요청이 진행 중입니다.");
        }

        DeletionRequest request = new DeletionRequest(user);
        request = deletionRequestRepository.save(request);

        user.setDeletionRequestedAt(LocalDateTime.now());
        userRepository.save(user);

        return request;
    }

    @Transactional
    public void cancelDeletionRequest(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        deletionRequestRepository.findByUserAndStatus(user, DeletionStatus.PENDING)
                .ifPresent(request -> {
                    request.cancel();
                    deletionRequestRepository.save(request);

                    user.setDeletionRequestedAt(null);
                    userRepository.save(user);
                });
    }

    @Transactional
    public void processDueForDeletion() {
        List<DeletionRequest> dueRequests = deletionRequestRepository.findDueForDeletion(
                DeletionStatus.PENDING, LocalDateTime.now());

        for (DeletionRequest request : dueRequests) {
            // 사용자 데이터 백업
            User user = request.getUser();
            Map<String, Object> backupData = Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "deletedAt", LocalDateTime.now().toString()
            );
            UserBackup backup = new UserBackup(user.getId(), backupData);
            userBackupRepository.save(backup);

            // 탈퇴 완료 처리
            request.complete();
            deletionRequestRepository.save(request);

            // 사용자 상태 변경
            user.setStatus(UserStatus.DELETED);
            userRepository.save(user);
        }
    }
}

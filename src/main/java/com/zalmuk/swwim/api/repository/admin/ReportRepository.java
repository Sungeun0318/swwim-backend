package com.zalmuk.swwim.api.repository.admin;

import com.zalmuk.swwim.api.entity.admin.Report;
import com.zalmuk.swwim.api.entity.enums.ReportStatus;
import com.zalmuk.swwim.api.entity.enums.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    Page<Report> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);

    Page<Report> findByReportTypeOrderByCreatedAtDesc(ReportType reportType, Pageable pageable);

    List<Report> findByTargetId(String targetId);

    long countByStatus(ReportStatus status);

    boolean existsByReporterIdAndTargetId(String reporterId, String targetId);
}

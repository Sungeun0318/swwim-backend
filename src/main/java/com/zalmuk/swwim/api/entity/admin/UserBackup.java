package com.zalmuk.swwim.api.entity.admin;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

/**
 * 사용자 백업 (탈퇴 전 백업)
 */
@Entity
@Table(name = "user_backups")
public class UserBackup extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId; // 탈퇴한 사용자의 ID (참조 없음)

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "backup_data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> backupData;

    // Constructors
    protected UserBackup() {
    }

    public UserBackup(String userId, Map<String, Object> backupData) {
        this.userId = userId;
        this.backupData = backupData;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Object> getBackupData() {
        return backupData;
    }

    public void setBackupData(Map<String, Object> backupData) {
        this.backupData = backupData;
    }
}

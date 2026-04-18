package com.zalmuk.swwim.api.entity.training;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import com.zalmuk.swwim.api.entity.enums.BackupType;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

/**
 * 훈련 백업
 */
@Entity
@Table(name = "training_backups")
public class TrainingBackup extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "backup_data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> backupData;

    @Enumerated(EnumType.STRING)
    @Column(name = "backup_type", length = 50)
    private BackupType backupType;

    // Constructors
    protected TrainingBackup() {
    }

    public TrainingBackup(User user, Map<String, Object> backupData, BackupType backupType) {
        this.user = user;
        this.backupData = backupData;
        this.backupType = backupType;
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

    public Map<String, Object> getBackupData() {
        return backupData;
    }

    public void setBackupData(Map<String, Object> backupData) {
        this.backupData = backupData;
    }

    public BackupType getBackupType() {
        return backupType;
    }

    public void setBackupType(BackupType backupType) {
        this.backupType = backupType;
    }
}

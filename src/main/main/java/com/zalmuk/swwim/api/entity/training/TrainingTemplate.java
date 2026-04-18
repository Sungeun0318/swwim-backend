package com.zalmuk.swwim.api.entity.training;

import com.zalmuk.swwim.api.entity.BaseEntity;
import com.zalmuk.swwim.api.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

/**
 * 훈련 템플릿 (재사용 가능한 훈련)
 */
@Entity
@Table(name = "training_templates", indexes = {
        @Index(name = "idx_training_templates_user", columnList = "user_id")
})
public class TrainingTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 공개 여부
    @Column(name = "is_public")
    private Boolean isPublic = false;

    // 템플릿 데이터 (JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "template_data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> templateData;

    // 사용 통계
    @Column(name = "use_count")
    private Integer useCount = 0;

    // Constructors
    protected TrainingTemplate() {
    }

    public TrainingTemplate(User user, String name, Map<String, Object> templateData) {
        this.user = user;
        this.name = name;
        this.templateData = templateData;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Map<String, Object> getTemplateData() {
        return templateData;
    }

    public void setTemplateData(Map<String, Object> templateData) {
        this.templateData = templateData;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public void incrementUseCount() {
        this.useCount++;
    }
}

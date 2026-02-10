package com.zalmuk.swwim.api.service.training;

import com.zalmuk.swwim.api.entity.training.TrainingTemplate;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.repository.training.TrainingTemplateRepository;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TrainingTemplateService {

    private final TrainingTemplateRepository templateRepository;
    private final UserRepository userRepository;

    public TrainingTemplateService(TrainingTemplateRepository templateRepository, UserRepository userRepository) {
        this.templateRepository = templateRepository;
        this.userRepository = userRepository;
    }

    public Optional<TrainingTemplate> findById(UUID id) {
        return templateRepository.findById(id);
    }

    public Page<TrainingTemplate> getUserTemplates(String userId, Pageable pageable) {
        return userRepository.findById(userId)
                .map(user -> templateRepository.findByUserOrderByCreatedAtDesc(user, pageable))
                .orElse(Page.empty());
    }

    public Page<TrainingTemplate> getPublicTemplates(Pageable pageable) {
        return templateRepository.findPublicTemplates(pageable);
    }

    public Page<TrainingTemplate> searchPublicTemplates(String keyword, Pageable pageable) {
        return templateRepository.searchPublicTemplates(keyword, pageable);
    }

    public Page<TrainingTemplate> getAvailableTemplates(String userId, Pageable pageable) {
        return templateRepository.findAvailableTemplates(userId, pageable);
    }

    @Transactional
    public TrainingTemplate createTemplate(String userId, String name, String description,
                                            Map<String, Object> templateData, boolean isPublic) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        TrainingTemplate template = new TrainingTemplate(user, name, templateData);
        template.setDescription(description);
        template.setIsPublic(isPublic);
        return templateRepository.save(template);
    }

    @Transactional
    public TrainingTemplate updateTemplate(UUID templateId, String userId, String name,
                                            String description, Map<String, Object> templateData, boolean isPublic) {
        TrainingTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("템플릿을 찾을 수 없습니다."));

        if (!template.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 템플릿만 수정할 수 있습니다.");
        }

        template.setName(name);
        template.setDescription(description);
        template.setTemplateData(templateData);
        template.setIsPublic(isPublic);
        return templateRepository.save(template);
    }

    @Transactional
    public void deleteTemplate(UUID templateId, String userId) {
        templateRepository.findById(templateId).ifPresent(template -> {
            if (!template.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("본인의 템플릿만 삭제할 수 있습니다.");
            }
            templateRepository.delete(template);
        });
    }

    @Transactional
    public void incrementUseCount(UUID templateId) {
        templateRepository.findById(templateId).ifPresent(template -> {
            template.incrementUseCount();
            templateRepository.save(template);
        });
    }
}

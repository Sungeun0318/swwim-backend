package com.zalmuk.swwim.api.service.quickstart;

import com.zalmuk.swwim.api.entity.enums.UserLevel;
import com.zalmuk.swwim.api.entity.quickstart.QuickstartDailyCompletion;
import com.zalmuk.swwim.api.entity.quickstart.QuickstartProgress;
import com.zalmuk.swwim.api.entity.quickstart.QuickstartWeekProgress;
import com.zalmuk.swwim.api.entity.quickstart.UserAchievement;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.repository.quickstart.*;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class QuickstartService {

    private final QuickstartProgressRepository progressRepository;
    private final QuickstartWeekProgressRepository weekProgressRepository;
    private final QuickstartDailyCompletionRepository dailyCompletionRepository;
    private final UserAchievementRepository achievementRepository;
    private final UserRepository userRepository;

    public QuickstartService(QuickstartProgressRepository progressRepository,
                              QuickstartWeekProgressRepository weekProgressRepository,
                              QuickstartDailyCompletionRepository dailyCompletionRepository,
                              UserAchievementRepository achievementRepository,
                              UserRepository userRepository) {
        this.progressRepository = progressRepository;
        this.weekProgressRepository = weekProgressRepository;
        this.dailyCompletionRepository = dailyCompletionRepository;
        this.achievementRepository = achievementRepository;
        this.userRepository = userRepository;
    }

    public Optional<QuickstartProgress> getProgress(String userId, UserLevel level) {
        return userRepository.findById(userId)
                .flatMap(user -> progressRepository.findByUserAndLevel(user, level));
    }

    public List<QuickstartProgress> getAllProgress(String userId) {
        return userRepository.findById(userId)
                .map(progressRepository::findByUserOrderByLevelAsc)
                .orElse(List.of());
    }

    @Transactional
    public QuickstartProgress startProgram(String userId, UserLevel level) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (progressRepository.existsByUserAndLevel(user, level)) {
            throw new IllegalStateException("이미 해당 레벨의 프로그램을 진행 중입니다.");
        }

        QuickstartProgress progress = new QuickstartProgress(user, level);
        progress = progressRepository.save(progress);

        // 첫 번째 주 생성 및 잠금 해제
        QuickstartWeekProgress week1 = new QuickstartWeekProgress(progress, 1);
        week1.setIsUnlocked(true);
        weekProgressRepository.save(week1);

        return progress;
    }

    @Transactional
    public QuickstartDailyCompletion completeDailyTraining(UUID progressId, int week,
                                                            int totalTime, int totalDistance) {
        QuickstartProgress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new IllegalArgumentException("진행 상황을 찾을 수 없습니다."));

        QuickstartWeekProgress weekProgress = weekProgressRepository.findByProgressAndWeek(progress, week)
                .orElseGet(() -> {
                    QuickstartWeekProgress newWeek = new QuickstartWeekProgress(progress, week);
                    return weekProgressRepository.save(newWeek);
                });

        if (!weekProgress.getIsUnlocked()) {
            throw new IllegalStateException("해당 주차가 아직 잠금 해제되지 않았습니다.");
        }

        LocalDate today = LocalDate.now();
        if (dailyCompletionRepository.existsByWeekProgressAndCompletionDate(weekProgress, today)) {
            throw new IllegalStateException("오늘의 훈련은 이미 완료했습니다.");
        }

        QuickstartDailyCompletion completion = new QuickstartDailyCompletion(weekProgress, today, totalTime, totalDistance);
        completion = dailyCompletionRepository.save(completion);

        // 주차 통계 업데이트
        weekProgress.setCompletedDays(weekProgress.getCompletedDays() + 1);
        weekProgress.setTotalTime(weekProgress.getTotalTime() + totalTime);
        weekProgress.setTotalDistance(weekProgress.getTotalDistance() + totalDistance);

        // 주차 완료 여부 확인 (5일 완료 시)
        if (weekProgress.getCompletedDays() >= 5) {
            weekProgress.setIsCompleted(true);
            weekProgress.setCompletedAt(LocalDateTime.now());

            // 다음 주 잠금 해제
            unlockNextWeek(progress, week);
        }

        weekProgressRepository.save(weekProgress);

        // 진행 상황 업데이트
        progress.setLastCompletedAt(LocalDateTime.now());
        progressRepository.save(progress);

        // 일일 완료 업적 부여
        grantAchievement(progress.getUser().getId(), "quickstart_daily", "day_" + weekProgress.getCompletedDays(), null);

        return completion;
    }

    private void unlockNextWeek(QuickstartProgress progress, int currentWeek) {
        int nextWeek = currentWeek + 1;
        if (nextWeek <= 4) { // 최대 4주
            weekProgressRepository.findByProgressAndWeek(progress, nextWeek)
                    .ifPresentOrElse(
                            wp -> {
                                wp.setIsUnlocked(true);
                                weekProgressRepository.save(wp);
                            },
                            () -> {
                                QuickstartWeekProgress newWeek = new QuickstartWeekProgress(progress, nextWeek);
                                newWeek.setIsUnlocked(true);
                                weekProgressRepository.save(newWeek);
                            }
                    );
            progress.setCurrentWeek(nextWeek);
            progressRepository.save(progress);
        }
    }

    // 업적 관련
    @Transactional
    public UserAchievement grantAchievement(String userId, String type, String name, Map<String, Object> data) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (achievementRepository.existsByUserAndAchievementTypeAndAchievementName(user, type, name)) {
            return null; // 이미 획득한 업적
        }

        UserAchievement achievement = new UserAchievement(user, type, name);
        achievement.setAchievementData(data);
        return achievementRepository.save(achievement);
    }

    public Page<UserAchievement> getUserAchievements(String userId, Pageable pageable) {
        return userRepository.findById(userId)
                .map(user -> achievementRepository.findByUserOrderByEarnedAtDesc(user, pageable))
                .orElse(Page.empty());
    }

    public boolean hasAchievement(String userId, String type, String name) {
        return userRepository.findById(userId)
                .map(user -> achievementRepository.existsByUserAndAchievementTypeAndAchievementName(user, type, name))
                .orElse(false);
    }
}

package com.zalmuk.swwim.api.dto.user;

import com.zalmuk.swwim.api.entity.user.UserStats;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 통계 응답 DTO
 */
@Schema(description = "사용자 통계 응답")
public class UserStatsResponse {

    @Schema(description = "사용자 ID")
    private String userId;

    // 전체 통계
    @Schema(description = "총 훈련 세션 수")
    private Integer totalSessions;

    @Schema(description = "총 수영 거리 (미터)")
    private Integer totalDistance;

    @Schema(description = "총 수영 시간 (초)")
    private Integer totalTime;

    // 월간 통계
    @Schema(description = "이번 달 훈련 세션 수")
    private Integer monthlySessions;

    @Schema(description = "이번 달 수영 거리 (미터)")
    private Integer monthlyDistance;

    @Schema(description = "이번 달 수영 시간 (초)")
    private Integer monthlyTime;

    // 연속 기록
    @Schema(description = "현재 연속 기록 (일)")
    private Integer currentStreak;

    @Schema(description = "최장 연속 기록 (일)")
    private Integer longestStreak;

    // 개인 기록
    @Schema(description = "100m 최고 기록 (초)")
    private Integer best100mTime;

    @Schema(description = "200m 최고 기록 (초)")
    private Integer best200mTime;

    @Schema(description = "500m 최고 기록 (초)")
    private Integer best500mTime;

    @Schema(description = "마지막 훈련 날짜")
    private LocalDate lastTrainingDate;

    @Schema(description = "통계 업데이트 시간")
    private LocalDateTime updatedAt;

    // Constructors
    public UserStatsResponse() {
    }

    public static UserStatsResponse from(UserStats stats) {
        UserStatsResponse response = new UserStatsResponse();
        response.setUserId(stats.getUserId());
        response.setTotalSessions(stats.getTotalSessions());
        response.setTotalDistance(stats.getTotalDistance());
        response.setTotalTime(stats.getTotalTime());
        response.setMonthlySessions(stats.getMonthlySessions());
        response.setMonthlyDistance(stats.getMonthlyDistance());
        response.setMonthlyTime(stats.getMonthlyTime());
        response.setCurrentStreak(stats.getCurrentStreak());
        response.setLongestStreak(stats.getLongestStreak());
        response.setBest100mTime(stats.getBest100mTime());
        response.setBest200mTime(stats.getBest200mTime());
        response.setBest500mTime(stats.getBest500mTime());
        response.setLastTrainingDate(stats.getLastTrainingDate());
        response.setUpdatedAt(stats.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Integer getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Integer totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getMonthlySessions() {
        return monthlySessions;
    }

    public void setMonthlySessions(Integer monthlySessions) {
        this.monthlySessions = monthlySessions;
    }

    public Integer getMonthlyDistance() {
        return monthlyDistance;
    }

    public void setMonthlyDistance(Integer monthlyDistance) {
        this.monthlyDistance = monthlyDistance;
    }

    public Integer getMonthlyTime() {
        return monthlyTime;
    }

    public void setMonthlyTime(Integer monthlyTime) {
        this.monthlyTime = monthlyTime;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(Integer longestStreak) {
        this.longestStreak = longestStreak;
    }

    public Integer getBest100mTime() {
        return best100mTime;
    }

    public void setBest100mTime(Integer best100mTime) {
        this.best100mTime = best100mTime;
    }

    public Integer getBest200mTime() {
        return best200mTime;
    }

    public void setBest200mTime(Integer best200mTime) {
        this.best200mTime = best200mTime;
    }

    public Integer getBest500mTime() {
        return best500mTime;
    }

    public void setBest500mTime(Integer best500mTime) {
        this.best500mTime = best500mTime;
    }

    public LocalDate getLastTrainingDate() {
        return lastTrainingDate;
    }

    public void setLastTrainingDate(LocalDate lastTrainingDate) {
        this.lastTrainingDate = lastTrainingDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

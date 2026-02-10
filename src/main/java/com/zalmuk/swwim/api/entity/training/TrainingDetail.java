package com.zalmuk.swwim.api.entity.training;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * 훈련 세부 항목 (TrainingDetailData)
 */
@Entity
@Table(name = "training_details", indexes = {
        @Index(name = "idx_training_details_session", columnList = "session_id")
})
public class TrainingDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private TrainingSession session;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "distance", nullable = false)
    private Integer distance; // 미터

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "cycle", nullable = false)
    private Integer cycle; // 초

    @Column(name = "rest_time", nullable = false)
    private Integer restTime = 0; // 초

    @Column(name = "\"interval\"", nullable = false)
    private Integer interval = 5; // 초

    @Column(name = "personnel", nullable = false)
    private Integer personnel = 1;

    // 순서
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    // Constructors
    protected TrainingDetail() {
    }

    public TrainingDetail(TrainingSession session, String title, Integer distance, Integer count, Integer cycle) {
        this.session = session;
        this.title = title;
        this.distance = distance;
        this.count = count;
        this.cycle = cycle;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public TrainingSession getSession() {
        return session;
    }

    public void setSession(TrainingSession session) {
        this.session = session;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }

    public Integer getRestTime() {
        return restTime;
    }

    public void setRestTime(Integer restTime) {
        this.restTime = restTime;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Integer personnel) {
        this.personnel = personnel;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}

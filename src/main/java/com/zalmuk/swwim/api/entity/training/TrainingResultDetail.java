package com.zalmuk.swwim.api.entity.training;

import com.zalmuk.swwim.api.entity.BaseTimeEntity;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * 훈련 결과 세부 항목
 */
@Entity
@Table(name = "training_result_details")
public class TrainingResultDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private TrainingResult result;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "distance", nullable = false)
    private Integer distance;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "cycle", nullable = false)
    private Integer cycle;

    @Column(name = "actual_time", nullable = false, length = 20)
    private String actualTime;

    @Column(name = "completed")
    private Boolean completed = true;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    // Constructors
    protected TrainingResultDetail() {
    }

    public TrainingResultDetail(TrainingResult result, String title, Integer distance, Integer count, Integer cycle, String actualTime) {
        this.result = result;
        this.title = title;
        this.distance = distance;
        this.count = count;
        this.cycle = cycle;
        this.actualTime = actualTime;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public TrainingResult getResult() {
        return result;
    }

    public void setResult(TrainingResult result) {
        this.result = result;
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

    public String getActualTime() {
        return actualTime;
    }

    public void setActualTime(String actualTime) {
        this.actualTime = actualTime;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}

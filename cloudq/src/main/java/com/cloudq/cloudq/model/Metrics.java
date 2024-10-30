package com.cloudq.cloudq.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "metrics")
public class Metrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId; // The resource to which these metrics relate

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName; // Name of the metric (e.g., "CPU Usage")

    @Column(name = "value", nullable = false)
    private Double value; // Value of the metric

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt; // When the metric was recorded

    // Constructors, Getters, and Setters

    public Metrics() {}

    public Metrics(Long resourceId, String metricName, Double value, LocalDateTime recordedAt) {
        this.resourceId = resourceId;
        this.metricName = metricName;
        this.value = value;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }

    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    @Override
    public String toString() {
        return "Metrics{" +
                "id=" + id +
                ", resourceId=" + resourceId +
                ", metricName='" + metricName + '\'' +
                ", value=" + value +
                ", recordedAt=" + recordedAt +
                '}';
    }
}
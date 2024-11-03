package com.cloudq.cloudq.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Model for Optimization Responses
 */
public class OptimizationResponse {

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("optimized_metrics")
    private Map<String, Object> optimizedMetrics;

    @JsonProperty("recommendations")
    private List<Recommendation> recommendations;

    // Constructors
    public OptimizationResponse() {}

    public OptimizationResponse(String requestId, LocalDateTime timestamp, Status status,
                                String message, Map<String, Object> optimizedMetrics,
                                List<Recommendation> recommendations) {
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.optimizedMetrics = optimizedMetrics;
        this.recommendations = recommendations;
    }

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getOptimizedMetrics() {
        return optimizedMetrics;
    }

    public void setOptimizedMetrics(Map<String, Object> optimizedMetrics) {
        this.optimizedMetrics = optimizedMetrics;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
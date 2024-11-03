package com.cloudq.cloudq.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

public class CloudMetrics {

    @JsonProperty("provider")
    private String provider;  // The name of the cloud provider (e.g., AWS, Azure, GCP)

    @JsonProperty("timestamp")
    private LocalDateTime timestamp; // Timestamp when the metrics were recorded

    @JsonProperty("metrics")
    private Map<String, Object> metrics; // A map to hold various metric values

    @JsonProperty("status")
    private String status; // Status of the metrics (e.g., OK, WARNING, ERROR)

    // Constructor
    public CloudMetrics(String provider, LocalDateTime timestamp, Map<String, Object> metrics, String status) {
        this.provider = provider;
        this.timestamp = timestamp;
        this.metrics = metrics;
        this.status = status;
    }

    // Getters and Setters
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Example of a method to update metrics
    public void updateMetric(String metricName, Object value) {
        this.metrics.put(metricName, value);
    }

    @Override
    public String toString() {
        return "CloudMetrics{" +
                "provider='" + provider + '\'' +
                ", timestamp=" + timestamp +
                ", metrics=" + metrics +
                ", status='" + status + '\'' +
                '}';
    }
}
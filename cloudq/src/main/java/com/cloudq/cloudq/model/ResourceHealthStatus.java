package com.cloudq.cloudq.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_health_status")
public class ResourceHealthStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id", nullable = false, length = 100)
    private String resourceId;

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType;

    @Column(name = "health_status", nullable = false, length = 20)
    private String healthStatus;

    @Column(name = "last_checked", nullable = false)
    private LocalDateTime lastChecked;

    @Column(name = "status_message", length = 255)
    private String statusMessage;

    // Constructors, Getters, and Setters

    public ResourceHealthStatus() {}

    public ResourceHealthStatus(String resourceId, String resourceType, String healthStatus, LocalDateTime lastChecked, String statusMessage) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.healthStatus = healthStatus;
        this.lastChecked = lastChecked;
        this.statusMessage = statusMessage;
    }

    public Long getId() { return id; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

    public LocalDateTime getLastChecked() { return lastChecked; }
    public void setLastChecked(LocalDateTime lastChecked) { this.lastChecked = lastChecked; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    @Override
    public String toString() {
        return "ResourceHealthStatus{" +
                "id=" + id +
                ", resourceId='" + resourceId + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", healthStatus='" + healthStatus + '\'' +
                ", lastChecked=" + lastChecked +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }
}
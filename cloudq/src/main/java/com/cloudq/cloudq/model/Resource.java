package com.cloudq.cloudq.model;


import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_name", nullable = false, length = 100)
    private String resourceName; // Name of the resource

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType; // Type of resource (e.g., "VM", "Database")

    @Column(name = "provider_id", nullable = false)
    private Long providerId; // Foreign key referencing the CloudProvider

    @Column(name = "status", nullable = false, length = 20)
    private String status; // Resource status (e.g., "ACTIVE", "INACTIVE")

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors, Getters, and Setters

    public Resource() {}

    public Resource(String resourceName, String resourceType, Long providerId, String status, LocalDateTime createdAt) {
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.providerId = providerId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", resourceName='" + resourceName + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", providerId=" + providerId +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

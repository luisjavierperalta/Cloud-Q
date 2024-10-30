package com.cloudq.cloudq.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cloud_providers")
public class CloudProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "provider_type", nullable = false, length = 50)
    private String providerType; // e.g., AWS, Azure, GCP

    @Column(name = "status", nullable = false, length = 20)
    private String status; // e.g., ACTIVE, INACTIVE

    @Column(name = "api_endpoint", length = 255)
    private String apiEndpoint; // Base API endpoint for the provider

    @Column(name = "credentials", length = 255)
    private String credentials; // Could be a reference to secure storage or encrypted details

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors, Getters, and Setters

    public CloudProvider() {}

    public CloudProvider(String name, String providerType, String status, String apiEndpoint, String credentials, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.providerType = providerType;
        this.status = status;
        this.apiEndpoint = apiEndpoint;
        this.credentials = credentials;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProviderType() { return providerType; }
    public void setProviderType(String providerType) { this.providerType = providerType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApiEndpoint() { return apiEndpoint; }
    public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }

    public String getCredentials() { return credentials; }
    public void setCredentials(String credentials) { this.credentials = credentials; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "CloudProvider{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", providerType='" + providerType + '\'' +
                ", status='" + status + '\'' +
                ", apiEndpoint='" + apiEndpoint + '\'' +
                ", credentials='" + credentials + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

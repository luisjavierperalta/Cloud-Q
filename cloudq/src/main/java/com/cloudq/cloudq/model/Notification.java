package com.cloudq.cloudq.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // The user to notify

    @Column(name = "message", nullable = false, length = 255)
    private String message; // Notification message

    @Column(name = "type", length = 50)
    private String type; // Type of notification (e.g., "INFO", "WARNING", "ERROR")

    @Column(name = "status", nullable = false, length = 20)
    private String status; // Status of notification (e.g., "READ", "UNREAD")

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors, Getters, and Setters

    public Notification() {}

    public Notification(Long userId, String message, String type, String status, LocalDateTime createdAt) {
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

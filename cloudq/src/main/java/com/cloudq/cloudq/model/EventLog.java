package com.cloudq.cloudq.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_logs")
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "log_level", nullable = false, length = 20)
    private String logLevel;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "message", nullable = false, length = 255)
    private String message;

    @Column(name = "details", length = 500)
    private String details;

    // Constructors, Getters, and Setters

    public EventLog() {}

    public EventLog(String eventType, String logLevel, LocalDateTime timestamp, String message, String details) {
        this.eventType = eventType;
        this.logLevel = logLevel;
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    public Long getId() { return id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getLogLevel() { return logLevel; }
    public void setLogLevel(String logLevel) { this.logLevel = logLevel; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    @Override
    public String toString() {
        return "EventLog{" +
                "id=" + id +
                ", eventType='" + eventType + '\'' +
                ", logLevel='" + logLevel + '\'' +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
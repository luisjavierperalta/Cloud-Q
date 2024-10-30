package com.cloudq.cloudq.model;


import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "optimization_tasks")
public class OptimizationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_type", nullable = false, length = 50)
    private String taskType; // Type of optimization task (e.g., "Resource Allocation")

    @Column(name = "parameters", nullable = false)
    private String parameters; // JSON or string representation of task parameters

    @Column(name = "status", nullable = false, length = 20)
    private String status; // Status of the task (e.g., "PENDING", "COMPLETED", "FAILED")

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime; // Time the task is scheduled to run

    @Column(name = "execution_time")
    private LocalDateTime executionTime; // Actual time the task was executed

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors, Getters, and Setters

    public OptimizationTask() {}

    public OptimizationTask(String taskType, String parameters, String status, LocalDateTime scheduledTime, LocalDateTime executionTime, LocalDateTime createdAt) {
        this.taskType = taskType;
        this.parameters = parameters;
        this.status = status;
        this.scheduledTime = scheduledTime;
        this.executionTime = executionTime;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public LocalDateTime getExecutionTime() { return executionTime; }
    public void setExecutionTime(LocalDateTime executionTime) { this.executionTime = executionTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "OptimizationTask{" +
                "id=" + id +
                ", taskType='" + taskType + '\'' +
                ", parameters='" + parameters + '\'' +
                ", status='" + status + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", executionTime=" + executionTime +
                ", createdAt=" + createdAt +
                '}';
    }
}

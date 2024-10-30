package com.cloudq.cloudq.ai.engine;


import java.util.HashMap;
import java.util.Map;

/**
 * Represents current resource usage data for monitoring and optimization.
 */
public class ResourceUsageData {
    private double cpuUsage;        // CPU usage as a percentage
    private double memoryUsage;     // Memory usage as a percentage
    private double diskUsage;       // Disk usage as a percentage
    private Map<String, Double> customMetrics; // Additional metrics

    // Constructor for initializing resource usage data
    public ResourceUsageData(double cpuUsage, double memoryUsage, double diskUsage) {
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskUsage = diskUsage;
        this.customMetrics = new HashMap<>();
    }

    // Getters and Setters
    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public double getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(double diskUsage) {
        this.diskUsage = diskUsage;
    }

    public Map<String, Double> getCustomMetrics() {
        return customMetrics;
    }

    // Method to add a custom metric
    public void addCustomMetric(String metricName, double value) {
        this.customMetrics.put(metricName, value);
    }

    // Method to remove a custom metric
    public void removeCustomMetric(String metricName) {
        this.customMetrics.remove(metricName);
    }

    // Method to get a specific custom metric
    public Double getCustomMetric(String metricName) {
        return this.customMetrics.get(metricName);
    }

    // Method to display resource usage data
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resource Usage Data:\n");
        sb.append("CPU Usage: ").append(cpuUsage).append("%\n");
        sb.append("Memory Usage: ").append(memoryUsage).append("%\n");
        sb.append("Disk Usage: ").append(diskUsage).append("%\n");

        if (!customMetrics.isEmpty()) {
            sb.append("Custom Metrics:\n");
            for (Map.Entry<String, Double> entry : customMetrics.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        return sb.toString();
    }
}

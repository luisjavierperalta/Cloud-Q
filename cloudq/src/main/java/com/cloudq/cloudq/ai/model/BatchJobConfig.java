package com.cloudq.cloudq.ai.model;

/**
 * Class representing the configuration for a batch job.
 */
public class BatchJobConfig {
    private String jobName;      // Name of the batch job
    private int frequency;       // Frequency of job execution in minutes
    private int duration;        // Duration for which the job runs in minutes
    private int resourceLimit;   // Resource limit for the job (e.g., memory, CPU)

    // Constructor to initialize BatchJobConfig
    public BatchJobConfig(String jobName, int frequency, int duration, int resourceLimit) {
        this.jobName = jobName;
        this.frequency = frequency;
        this.duration = duration;
        this.resourceLimit = resourceLimit;
    }

    // Getter for jobName
    public String getJobName() {
        return jobName;
    }

    // Setter for jobName
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    // Getter for frequency
    public int getFrequency() {
        return frequency;
    }

    // Setter for frequency
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    // Getter for duration
    public int getDuration() {
        return duration;
    }

    // Setter for duration
    public void setDuration(int duration) {
        this.duration = duration;
    }

    // Getter for resourceLimit
    public int getResourceLimit() {
        return resourceLimit;
    }

    // Setter for resourceLimit
    public void setResourceLimit(int resourceLimit) {
        this.resourceLimit = resourceLimit;
    }

    @Override
    public String toString() {
        return "BatchJobConfig{" +
                "jobName='" + jobName + '\'' +
                ", frequency=" + frequency +
                ", duration=" + duration +
                ", resourceLimit=" + resourceLimit +
                '}';
    }
}

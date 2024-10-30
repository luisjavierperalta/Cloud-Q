package com.cloudq.cloudq.ai.service;

import com.cloudq.cloudq.ai.engine.ModelTrainer;
import com.cloudq.cloudq.ai.model.ModelTrainingJob;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ModelTrainingService {
    private static final Logger logger = LoggerFactory.getLogger(ModelTrainingService.class);

    private final ModelTrainer modelTrainer;
    private final List<ModelTrainingJob> scheduledJobs;

    // Constructor to initialize ModelTrainingService with a Spark session
    public ModelTrainingService(SparkSession spark) {
        if (spark == null) {
            throw new IllegalArgumentException("SparkSession cannot be null");
        }
        this.modelTrainer = new ModelTrainer(spark);
        this.scheduledJobs = new ArrayList<>();
    }

    // Method to schedule a new model training job
    public void scheduleTrainingJob(ModelTrainingJob job) {
        if (job == null) {
            throw new IllegalArgumentException("ModelTrainingJob cannot be null");
        }
        scheduledJobs.add(job);
        logger.info("Scheduled training job for model: {}", job.getModelName());
    }

    // Method to start all scheduled training jobs
    public void executeScheduledJobs() {
        for (ModelTrainingJob job : scheduledJobs) {
            try {
                logger.info("Starting training for model: {}", job.getModelName());
                modelTrainer.trainModel(job);
                logger.info("Training completed for model: {}", job.getModelName());
            } catch (Exception e) {
                logger.error("Failed to train model: {}. Error: {}", job.getModelName(), e.getMessage());
            }
        }
        scheduledJobs.clear();  // Clear scheduled jobs after execution
    }

    // Method to remove a scheduled job by model name
    public boolean removeScheduledJob(String modelName) {
        return scheduledJobs.removeIf(job -> job.getModelName().equalsIgnoreCase(modelName));
    }

    // Method to list all scheduled jobs
    public List<ModelTrainingJob> getScheduledJobs() {
        return new ArrayList<>(scheduledJobs); // Return a copy to prevent modification
    }
}

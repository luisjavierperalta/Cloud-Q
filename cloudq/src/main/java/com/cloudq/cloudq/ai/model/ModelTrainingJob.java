package com.cloudq.cloudq.ai.model;


/**
 * Class representing a job for training a machine learning model.
 */
public class ModelTrainingJob {
    private String modelName;          // Name of the model to be trained
    private String trainingDataPath;   // Path to the training data
    private String modelOutputPath;    // Path where the trained model will be saved

    // Constructor to initialize ModelTrainingJob
    public ModelTrainingJob(String modelName, String trainingDataPath, String modelOutputPath) {
        this.modelName = modelName;
        this.trainingDataPath = trainingDataPath;
        this.modelOutputPath = modelOutputPath;
    }

    // Getter for modelName
    public String getModelName() {
        return modelName;
    }

    // Setter for modelName
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    // Getter for trainingDataPath
    public String getTrainingDataPath() {
        return trainingDataPath;
    }

    // Setter for trainingDataPath
    public void setTrainingDataPath(String trainingDataPath) {
        this.trainingDataPath = trainingDataPath;
    }

    // Getter for modelOutputPath
    public String getModelOutputPath() {
        return modelOutputPath;
    }

    // Setter for modelOutputPath
    public void setModelOutputPath(String modelOutputPath) {
        this.modelOutputPath = modelOutputPath;
    }

    @Override
    public String toString() {
        return "ModelTrainingJob{" +
                "modelName='" + modelName + '\'' +
                ", trainingDataPath='" + trainingDataPath + '\'' +
                ", modelOutputPath='" + modelOutputPath + '\'' +
                '}';
    }
}

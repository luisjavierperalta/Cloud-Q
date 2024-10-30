package com.cloudq.cloudq.ai.model;


/**
 * Class representing a report for model evaluation.
 */
public class ModelEvaluationReport {
    private String modelName; // Name of the model being evaluated
    private ModelMetrics metrics; // Metrics associated with the model's evaluation

    // Constructor to initialize ModelEvaluationReport
    public ModelEvaluationReport(String modelName, ModelMetrics metrics) {
        this.modelName = modelName;
        this.metrics = metrics;
    }

    // Getter for modelName
    public String getModelName() {
        return modelName;
    }

    // Setter for modelName
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    // Getter for metrics
    public ModelMetrics getMetrics() {
        return metrics;
    }

    // Setter for metrics
    public void setMetrics(ModelMetrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public String toString() {
        return "ModelEvaluationReport{" +
                "modelName='" + modelName + '\'' +
                ", metrics=" + metrics +
                '}';
    }
}

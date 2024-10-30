package com.cloudq.cloudq.ai.engine;


import com.cloudq.cloudq.ai.model.AIModel;
import com.cloudq.cloudq.ai.model.ModelMetrics;
import com.cloudq.cloudq.ai.model.ModelEvaluationReport;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.HashMap;
import java.util.Map;

public class ModelEvaluator {

    // Map to store metrics for each model name (useful for tracking performance history)
    private Map<String, ModelEvaluationReport> evaluationReports;

    public ModelEvaluator() {
        this.evaluationReports = new HashMap<>();
    }

    /**
     * Evaluates a given AIModel on a test dataset and stores the result.
     *
     * @param modelName - The name of the model being evaluated
     * @param model - The AIModel to evaluate
     * @param testData - The test dataset to use for evaluation
     * @param testLabels - The true labels for the test dataset
     * @return ModelEvaluationReport - An evaluation report containing performance metrics
     */
    public ModelEvaluationReport evaluateModel(String modelName, AIModel model, Dataset<Row> testData, double[] testLabels) {
        // Convert test data to the format required by the model (typically a double[][] array)
        double[][] testDataArray = convertDatasetTo2DArray(testData);

        // Perform the evaluation
        ModelMetrics metrics = model.evaluate(testDataArray, testLabels);

        // Create an evaluation report with model name and metrics
        ModelEvaluationReport report = new ModelEvaluationReport(modelName, metrics);

        // Store the report for this model
        evaluationReports.put(modelName, report);

        // Optionally, log the metrics for real-time tracking
        logMetrics(modelName, metrics);

        return report;
    }

    /**
     * Logs metrics to the console or a file for tracking (optional).
     * In production, this can be replaced by a monitoring service like Prometheus or ELK.
     *
     * @param modelName - The name of the model
     * @param metrics - The metrics to log
     */
    private void logMetrics(String modelName, ModelMetrics metrics) {
        System.out.println("Model Evaluation for: " + modelName);
        System.out.println("Precision: " + metrics.getPrecision());
        System.out.println("Recall: " + metrics.getRecall());

    }

    /**
     * Retrieves the latest evaluation report for a given model.
     *
     * @param modelName - The name of the model
     * @return ModelEvaluationReport - The latest evaluation report for the model
     */
    public ModelEvaluationReport getLatestReport(String modelName) {
        return evaluationReports.get(modelName);
    }

    /**
     * Converts a Spark Dataset<Row> to a 2D array (double[][]).
     * This method assumes the Dataset contains numerical features only.
     *
     * @param dataset - The Spark Dataset to convert
     * @return double[][] - 2D array representing the dataset
     */
    private double[][] convertDatasetTo2DArray(Dataset<Row> dataset) {
        return dataset.collectAsList().stream()
                .map(row -> {
                    double[] features = new double[row.size()];
                    for (int i = 0; i < row.size(); i++) {
                        features[i] = row.getDouble(i);
                    }
                    return features;
                })
                .toArray(double[][]::new);
    }

    /**
     * Clears all evaluation reports (useful for retraining scenarios).
     */
    public void clearReports() {
        evaluationReports.clear();
    }

    /**
     * Gets all stored evaluation reports for review or analysis.
     *
     * @return Map<String, ModelEvaluationReport> - A map of model names to their evaluation reports
     */
    public Map<String, ModelEvaluationReport> getAllReports() {
        return evaluationReports;
    }
}

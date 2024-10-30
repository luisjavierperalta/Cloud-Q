package com.cloudq.cloudq.ai.engine;


import com.cloudq.cloudq.ai.model.ModelEvaluationReport;
import com.cloudq.cloudq.ai.model.ModelMetrics;
import com.cloudq.cloudq.ai.model.OptimizationRequest;
import com.cloudq.cloudq.ai.model.OptimizationResponse;
import com.cloudq.cloudq.ai.model.PredictiveAnalyticsModel;
import com.cloudq.cloudq.ai.model.OptimizationAlgorithmModel;
import java.util.logging.Logger;

public class EvaluationEngine {

    private static final Logger logger = Logger.getLogger(EvaluationEngine.class.getName());

    private final PredictiveAnalyticsModel predictiveModel;
    private final OptimizationAlgorithmModel optimizationModel;

    public EvaluationEngine(PredictiveAnalyticsModel predictiveModel, OptimizationAlgorithmModel optimizationModel) {
        this.predictiveModel = predictiveModel;
        this.optimizationModel = optimizationModel;
    }

    public ModelEvaluationReport evaluatePredictiveModel(double[][] testData, double[] testLabels) {
        if (testData == null || testLabels == null || testLabels.length == 0) {
            logger.warning("Invalid test data or labels provided for evaluation.");
            return null;
        }

        try {
            double[] predictions = predictiveModel.predict(testData);  // Now matches double[][] format
            ModelMetrics metrics = calculateMetrics(predictions, testLabels);
            return new ModelEvaluationReport(predictiveModel.getClass().getSimpleName(), metrics);
        } catch (Exception e) {
            logger.severe("Error during model evaluation: " + e.getMessage());
            return null;
        }
    }

    public ModelEvaluationReport evaluateOptimization(OptimizationRequest request, OptimizationResponse response) {
        if (request == null || response == null) {
            logger.warning("Invalid optimization request or response provided for evaluation.");
            return null;
        }

        try {
            double costDeviation = calculateDeviation(request.getTargetCost(), parseAchievedCost(response));
            double performanceDeviation = calculateDeviation(request.getDesiredPerformance(), parseAchievedPerformance(response));

            ModelMetrics metrics = new ModelMetrics();
            metrics.setCostDeviation(costDeviation);         // Ensure these setters are present in ModelMetrics
            metrics.setPerformanceDeviation(performanceDeviation);

            return new ModelEvaluationReport(optimizationModel.getClass().getSimpleName(), metrics);
        } catch (Exception e) {
            logger.severe("Error during optimization evaluation: " + e.getMessage());
            return null;
        }
    }

    private ModelMetrics calculateMetrics(double[] predictions, double[] labels) {
        int correct = 0;
        int total = labels.length;
        for (int i = 0; i < total; i++) {
            if (predictions[i] == labels[i]) correct++;
        }
        double accuracy = (double) correct / total;

        ModelMetrics metrics = new ModelMetrics();
        metrics.setAccuracy(accuracy);  // Ensure setAccuracy method exists in ModelMetrics
        return metrics;
    }

    private double calculateDeviation(double target, double achieved) {
        return Math.abs(target - achieved) / target;
    }

    private double parseAchievedCost(OptimizationResponse response) {
        // Real parsing logic to extract achieved cost from response
        return 0.0;
    }

    private double parseAchievedPerformance(OptimizationResponse response) {
        // Real parsing logic to extract achieved performance from response
        return 0.0;
    }

    public void logEvaluationReport(ModelEvaluationReport report) {
        if (report != null) {
            logger.info("Evaluation Report for " + report.getModelName() + ": " + report.getMetrics());
        } else {
            logger.warning("No evaluation report generated.");
        }
    }
}

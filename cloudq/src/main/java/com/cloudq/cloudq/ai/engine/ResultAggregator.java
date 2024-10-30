package com.cloudq.cloudq.ai.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultAggregator {

    private static final Logger logger = Logger.getLogger(ResultAggregator.class.getName());

    // Thread-safe list to hold individual prediction results
    private final List<PredictionResult> results;

    public ResultAggregator() {
        results = new CopyOnWriteArrayList<>(); // Thread-safe collection
    }

    // Method to add a prediction result
    public void addResult(PredictionResult result) {
        if (result == null) {
            logger.log(Level.WARNING, "Attempted to add a null PredictionResult.");
            return;
        }
        results.add(result);
        logger.log(Level.INFO, "Added PredictionResult: " + result);
    }

    // Method to aggregate results based on some criteria
    public AggregatedResult aggregate() {
        double totalCost = 0.0;
        double totalPerformance = 0.0;
        int count = results.size();

        if (count == 0) {
            logger.log(Level.WARNING, "No results to aggregate.");
            return new AggregatedResult(0.0, 0.0, 0);
        }

        for (PredictionResult result : results) {
            totalCost += result.getEstimatedCost();
            totalPerformance += result.getPerformanceScore();
        }

        double averageCost = totalCost / count;
        double averagePerformance = totalPerformance / count;

        logger.log(Level.INFO, "Aggregated Results - Average Cost: " + averageCost +
                ", Average Performance: " + averagePerformance + ", Count: " + count);

        return new AggregatedResult(averageCost, averagePerformance, count);
    }

    // Method to get the detailed results
    public List<PredictionResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    // Optional: Clear all results
    public void clearResults() {
        results.clear();
        logger.log(Level.INFO, "All results cleared.");
    }
}

// Class to hold individual prediction results
class PredictionResult {
    private final double estimatedCost;   // Estimated cost for the workload
    private final double performanceScore; // Performance score based on resource utilization

    public PredictionResult(double estimatedCost, double performanceScore) {
        if (estimatedCost < 0 || performanceScore < 0) {
            throw new IllegalArgumentException("Estimated cost and performance score must be non-negative.");
        }
        this.estimatedCost = estimatedCost;
        this.performanceScore = performanceScore;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public double getPerformanceScore() {
        return performanceScore;
    }

    @Override
    public String toString() {
        return "PredictionResult{" +
                "estimatedCost=" + estimatedCost +
                ", performanceScore=" + performanceScore +
                '}';
    }
}

// Class to hold aggregated result
class AggregatedResult {
    private final double averageCost;        // Average cost of all predictions
    private final double averagePerformance;  // Average performance score
    private final int resultCount;           // Number of results aggregated

    public AggregatedResult(double averageCost, double averagePerformance, int resultCount) {
        this.averageCost = averageCost;
        this.averagePerformance = averagePerformance;
        this.resultCount = resultCount;
    }

    public double getAverageCost() {
        return averageCost;
    }

    public double getAveragePerformance() {
        return averagePerformance;
    }

    public int getResultCount() {
        return resultCount;
    }

    @Override
    public String toString() {
        return "AggregatedResult{" +
                "averageCost=" + averageCost +
                ", averagePerformance=" + averagePerformance +
                ", resultCount=" + resultCount +
                '}';
    }
}

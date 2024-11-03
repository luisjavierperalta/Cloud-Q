package com.cloudq.cloudq.util;

import java.util.HashMap;
import java.util.Map;

public class OptimizationUtil {

    /**
     * Calculates the cost savings by comparing initial and optimized costs.
     *
     * @param initialCost Original cost before optimization.
     * @param optimizedCost Cost after optimization.
     * @return The calculated cost savings.
     * @throws IllegalArgumentException if initial or optimized cost is negative.
     */
    public static double calculateCostSavings(double initialCost, double optimizedCost) {
        if (initialCost < 0 || optimizedCost < 0) {
            throw new IllegalArgumentException("Costs cannot be negative.");
        }
        return initialCost - optimizedCost;
    }

    /**
     * Computes an optimization score based on user preferences and metrics.
     *
     * @param userPreferences User-defined preferences for optimization (e.g., "cost" or "performance").
     * @param metrics Current metrics data.
     * @return A score that ranks the recommendation based on preferences.
     */
    public static double computeOptimizationScore(Map<String, String> userPreferences, Map<String, Object> metrics) {
        double score = 0.0;
        String preference = userPreferences.getOrDefault("preference", "cost");

        if ("cost".equalsIgnoreCase(preference)) {
            score = (double) metrics.getOrDefault("costSavings", 0.0);
        } else if ("performance".equalsIgnoreCase(preference)) {
            score = (double) metrics.getOrDefault("performanceScore", 0.0);
        }

        return score;
    }

    /**
     * Optimizes resources based on workload parameters.
     *
     * @param workloadParameters Parameters such as CPU, memory, and storage requirements.
     * @return Map containing optimized resources allocation.
     */
    public static Map<String, Object> optimizeResources(Map<String, String> workloadParameters) {
        Map<String, Object> optimizedResources = new HashMap<>();

        int cpu = Integer.parseInt(workloadParameters.getOrDefault("cpu", "1"));
        int memory = Integer.parseInt(workloadParameters.getOrDefault("memory", "1024"));
        int storage = Integer.parseInt(workloadParameters.getOrDefault("storage", "50000"));

        // Example optimization logic: Scale resources
        optimizedResources.put("cpu", Math.max(1, cpu / 2));
        optimizedResources.put("memory", memory / 2);
        optimizedResources.put("storage", storage / 2);

        return optimizedResources;
    }
}
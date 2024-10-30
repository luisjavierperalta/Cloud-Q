package com.cloudq.cloudq.ai.model;


public class OptimizationTask {
    private final double[] bounds; // An array holding the min and max bounds for optimization
    private final String function;  // The mathematical function to optimize (as a string)

    public OptimizationTask(double[] bounds, String function) {
        this.bounds = bounds;
        this.function = function;
    }

    public double[] getBounds() {
        return bounds;
    }

    public String getFunction() {
        return function;
    }
}


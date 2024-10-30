package com.cloudq.cloudq.ai.model;

public class OptimizationRequest {
    private String requestId;
    private double targetCost;
    private double desiredPerformance;

    // Constructor
    public OptimizationRequest(String requestId, double targetCost, double desiredPerformance) {
        this.requestId = requestId;
        this.targetCost = targetCost;
        this.desiredPerformance = desiredPerformance;
    }

    public OptimizationRequest() {

    }

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public double getTargetCost() {
        return targetCost;
    }

    public void setTargetCost(double targetCost) {
        this.targetCost = targetCost;
    }

    public double getDesiredPerformance() {
        return desiredPerformance;
    }

    public void setDesiredPerformance(double desiredPerformance) {
        this.desiredPerformance = desiredPerformance;
    }
}

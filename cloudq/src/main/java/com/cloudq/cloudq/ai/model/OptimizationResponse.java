package com.cloudq.cloudq.ai.model;


public class OptimizationResponse {
    private String requestId;
    private String status;
    private String[] recommendedActions;

    // Constructor
    public OptimizationResponse(String requestId, String status, String[] recommendedActions) {
        this.requestId = requestId;
        this.status = status;
        this.recommendedActions = recommendedActions;
    }

    public OptimizationResponse() {

    }

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getRecommendedActions() {
        return recommendedActions;
    }

    public void setRecommendedActions(String[] recommendedActions) {
        this.recommendedActions = recommendedActions;
    }
}

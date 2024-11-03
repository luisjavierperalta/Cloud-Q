package com.cloudq.cloudq.model;

/**
 * Data Model for Optimization Recommendations
 */
public class Recommendation {

    private String recommendationId;
    private String description;

    // Constructors
    public Recommendation() {}

    public Recommendation(String recommendationId, String description) {
        this.recommendationId = recommendationId;
        this.description = description;
    }

    // Getters and Setters
    public String getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(String recommendationId) {
        this.recommendationId = recommendationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
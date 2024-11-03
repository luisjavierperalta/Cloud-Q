package com.cloudq.cloudq.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OptimizationRecommendation {

    @JsonProperty("id")
    private String id; // Unique identifier for the recommendation

    @JsonProperty("description")
    private String description; // Description of the recommendation

    @JsonProperty("impact")
    private String impact; // Potential impact of following the recommendation (e.g., "high", "medium", "low")

    @JsonProperty("action")
    private String action; // Suggested action to implement the recommendation

    // Constructor
    public OptimizationRecommendation(String id, String description, String impact, String action) {
        this.id = id;
        this.description = description;
        this.impact = impact;
        this.action = action;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "OptimizationRecommendation{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", impact='" + impact + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
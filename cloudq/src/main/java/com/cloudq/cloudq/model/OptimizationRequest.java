package com.cloudq.cloudq.model;

import java.io.Serializable;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Represents a request for optimization with details about cloud resources, workload, and user preferences.
 */
@ApiModel(description = "Data Model for Optimization Requests")
public class OptimizationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Unique identifier for the optimization request", example = "12345", required = true)
    @NotBlank(message = "Request ID cannot be blank")
    private String requestId;

    @ApiModelProperty(value = "Cloud provider for optimization (AWS, Azure, GCP, IBM)", example = "AWS", required = true)
    @NotBlank(message = "Cloud provider cannot be blank")
    @Size(min = 2, max = 20, message = "Cloud provider name length should be between 2 and 20 characters")
    private String cloudProvider;

    @ApiModelProperty(value = "Target cost or budget for optimization", example = "1000.00", required = true)
    @NotNull(message = "Target cost cannot be null")
    @Positive(message = "Target cost must be positive")
    private Double targetCost;

    @ApiModelProperty(value = "Map of workload parameters such as CPU, memory, and storage requirements", example = "{\"cpu\": \"4\", \"memory\": \"16GB\", \"storage\": \"500GB\"}", required = true)
    @NotNull(message = "Workload parameters cannot be null")
    private Map<String, String> workloadParameters;

    @ApiModelProperty(value = "User preferences for optimization, e.g., focus on cost or performance", example = "{\"preference\": \"cost\"}")
    private Map<String, String> userPreferences;

    @ApiModelProperty(value = "Description of the optimization goal or additional notes", example = "Optimize for cost while maintaining performance")
    @Size(max = 200, message = "Description length cannot exceed 200 characters")
    private String description;

    // Default constructor
    public OptimizationRequest() {
    }

    // Parameterized constructor
    public OptimizationRequest(String requestId, String cloudProvider, Double targetCost,
                               Map<String, String> workloadParameters,
                               Map<String, String> userPreferences, String description) {
        this.requestId = requestId;
        this.cloudProvider = cloudProvider;
        this.targetCost = targetCost;
        this.workloadParameters = workloadParameters;
        this.userPreferences = userPreferences;
        this.description = description;
    }

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCloudProvider() {
        return cloudProvider;
    }

    public void setCloudProvider(String cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

    public Double getTargetCost() {
        return targetCost;
    }

    public void setTargetCost(Double targetCost) {
        this.targetCost = targetCost;
    }

    public Map<String, String> getWorkloadParameters() {
        return workloadParameters;
    }

    public void setWorkloadParameters(Map<String, String> workloadParameters) {
        this.workloadParameters = workloadParameters;
    }

    public Map<String, String> getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(Map<String, String> userPreferences) {
        this.userPreferences = userPreferences;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Custom methods and logic (if any) could be added here for processing within the OptimizationRequest context.

    @Override
    public String toString() {
        return "OptimizationRequest{" +
                "requestId='" + requestId + '\'' +
                ", cloudProvider='" + cloudProvider + '\'' +
                ", targetCost=" + targetCost +
                ", workloadParameters=" + workloadParameters +
                ", userPreferences=" + userPreferences +
                ", description='" + description + '\'' +
                '}';
    }
}
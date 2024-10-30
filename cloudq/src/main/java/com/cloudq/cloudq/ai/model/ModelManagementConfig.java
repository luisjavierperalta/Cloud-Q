package com.cloudq.cloudq.ai.model;


/**
 * Class representing the configuration for model management.
 */
public class ModelManagementConfig {
    private String modelVersion;          // Version of the model
    private String deploymentStrategy;     // Strategy used for deploying the model
    private boolean enableMonitoring;      // Flag to enable or disable monitoring

    // Constructor to initialize ModelManagementConfig
    public ModelManagementConfig(String modelVersion, String deploymentStrategy, boolean enableMonitoring) {
        this.modelVersion = modelVersion;
        this.deploymentStrategy = deploymentStrategy;
        this.enableMonitoring = enableMonitoring;
    }

    // Getter for modelVersion
    public String getModelVersion() {
        return modelVersion;
    }

    // Setter for modelVersion
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    // Getter for deploymentStrategy
    public String getDeploymentStrategy() {
        return deploymentStrategy;
    }

    // Setter for deploymentStrategy
    public void setDeploymentStrategy(String deploymentStrategy) {
        this.deploymentStrategy = deploymentStrategy;
    }

    // Getter for enableMonitoring
    public boolean isEnableMonitoring() {
        return enableMonitoring;
    }

    // Setter for enableMonitoring
    public void setEnableMonitoring(boolean enableMonitoring) {
        this.enableMonitoring = enableMonitoring;
    }

    @Override
    public String toString() {
        return "ModelManagementConfig{" +
                "modelVersion='" + modelVersion + '\'' +
                ", deploymentStrategy='" + deploymentStrategy + '\'' +
                ", enableMonitoring=" + enableMonitoring +
                '}';
    }
}

package com.cloudq.cloudq.ai.service.optimization;


import java.util.Map;

public interface CloudOptimizationService {

    /**
     * Collects data about AWS resources and their metrics.
     *
     * @return a map containing resource data, including total instances and average CPU utilization.
     */
    Map<String, Object> collectData();

    /**
     * Optimizes resources based on the provided data.
     *
     * @param awsData a map containing AWS resource metrics, such as total instances and average CPU utilization.
     */
    void optimizeResources(Map<String, Object> awsData);

    /**
     * Launches a new EC2 instance based on pre-defined configurations.
     */
    void launchNewInstance();

    /**
     * Terminates underutilized EC2 instances based on performance metrics.
     */
    void terminateUnderutilizedInstance();
}

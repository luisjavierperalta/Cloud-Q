package com.cloudq.cloudq.service;

import java.util.List;
import java.util.Map;

/**
 * Interface representing a client for cloud providers.
 * This interface allows for interaction with various cloud services in a standardized way.
 */
public interface CloudProviderClient {

    /**
     * Launches a cloud instance based on the provided workload parameters.
     *
     * @param workloadParameters A map containing parameters needed for launching the instance,
     *                           such as CPU, memory, and other configuration options.
     */
    void launchInstance(Map<String, String> workloadParameters);

    /**
     * Retrieves the current metrics from the cloud provider.
     *
     * @return A map containing key-value pairs of various metrics,
     *         such as CPU usage, memory consumption, and disk I/O.
     */
    Map<String, Object> getMetrics();

    /**
     * Stops a running cloud instance.
     *
     * @param instanceId The ID of the instance to stop.
     */
    void stopInstance(String instanceId);

    /**
     * Terminates a cloud instance permanently.
     *
     * @param instanceId The ID of the instance to terminate.
     */
    void terminateInstance(String instanceId);

    /**
     * Retrieves details about a specific cloud instance.
     *
     * @param instanceId The ID of the instance to retrieve details for.
     * @return A map containing details about the instance, such as its status, type, etc.
     */
    Map<String, Object> getInstanceDetails(String instanceId);

    /**
     * Lists all running instances under the cloud provider.
     *
     * @return A list of maps, each containing details about a running instance.
     */
    List<Map<String, Object>> listRunningInstances();

    // You can add more methods as necessary for other common cloud operations
}
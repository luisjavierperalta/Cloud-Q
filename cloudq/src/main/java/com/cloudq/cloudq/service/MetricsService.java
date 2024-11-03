package com.cloudq.cloudq.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
     * Interface for a service that fetches metrics from cloud resources.
     * This interface can be implemented for different cloud providers to retrieve relevant metrics.
     */
    public interface MetricsService {

    Map<String, Object> fetchMetrics(String resourceId);

    /**
         * Fetch metrics for a specified resource.
         *
         * @param resourceId The unique identifier of the resource for which metrics are to be fetched.
         * @param instant
         * @param now
         * @return A map containing the fetched metrics. The structure of the map will depend on the cloud provider's metrics.
         */
        Map<String, Object> fetchMetrics(String resourceId, Instant instant, Instant now);

        /**
         * Fetch metrics for a specified resource over a given time range.
         * This method can be used to get historical metrics or metrics for specific intervals.
         *
         * @param resourceId The unique identifier of the resource for which metrics are to be fetched.
         * @param startTime The start time for the metric fetch.
         * @param endTime The end time for the metric fetch.
         * @return A map containing the fetched metrics over the specified time range.
         */
        Map<String, Object> fetchMetrics(String resourceId, long startTime, long endTime);

        /**
         * Fetch metrics for multiple resources.
         *
         * @param resourceIds A list of unique identifiers for the resources.
         * @return A map where each key is a resource ID and the value is a map of metrics for that resource.
         */
        Map<String, Map<String, Object>> fetchMetricsForMultipleResources(List<String> resourceIds);

        // Additional methods can be added as needed
    }
package com.cloudq.cloudq.util;

import java.util.HashMap;
import java.util.Map;

public class MetricsUtil {

    /**
     * Formats CPU usage as a percentage.
     *
     * @param cpuUsage CPU usage as a decimal (e.g., 0.5 for 50%).
     * @return CPU usage formatted as a percentage string.
     */
    public static String formatCpuUsage(double cpuUsage) {
        return String.format("%.2f%%", cpuUsage * 100);
    }

    /**
     * Converts memory usage from megabytes (MB) to gigabytes (GB).
     *
     * @param memoryInMb Memory in megabytes.
     * @return Memory in gigabytes.
     */
    public static double normalizeMemoryUsage(double memoryInMb) {
        return memoryInMb / 1024.0;
    }

    /**
     * Aggregates multiple metrics sources into a single map.
     * This method combines metrics data from various sources.
     *
     * @param metrics Array of metrics maps to aggregate.
     * @return Aggregated map containing combined metrics.
     */
    public static Map<String, Object> aggregateMetrics(Map<String, Object>... metrics) {
        Map<String, Object> aggregatedMetrics = new HashMap<>();
        for (Map<String, Object> metric : metrics) {
            if (metric != null) {
                aggregatedMetrics.putAll(metric);
            }
        }
        return aggregatedMetrics;
    }

    /**
     * Converts storage size from bytes to gigabytes, with error handling for invalid inputs.
     *
     * @param storageInBytes Storage size in bytes.
     * @return Storage size in gigabytes.
     * @throws IllegalArgumentException if storageInBytes is negative.
     */
    public static double convertStorageToGb(long storageInBytes) {
        if (storageInBytes < 0) {
            throw new IllegalArgumentException("Storage size cannot be negative.");
        }
        return storageInBytes / (1024.0 * 1024.0 * 1024.0);
    }
}
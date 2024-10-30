package com.cloudq.cloudq.ai.service;

import com.cloudq.cloudq.ai.service.optimization.CloudOptimizationService;
import com.cloudq.cloudq.ai.service.optimization.GCPOptimizationService;
import com.cloudq.cloudq.ai.service.optimization.IBMCloudOptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

    @Autowired
    private IBMCloudOptimizationService ibmService;

    @Autowired
    private GCPOptimizationService gcpService;

    @Autowired
    private CloudOptimizationService awsService;

    // Inject other cloud services as needed
    // @Autowired private AzureCloudOptimizationService azureService;
    // @Autowired private OracleCloudOptimizationService oracleService;

    private static final double HIGH_CPU_THRESHOLD = 80.0;
    private static final double LOW_CPU_THRESHOLD = 30.0;

    /**
     * Generates recommendations based on the workload data across multiple cloud providers.
     *
     * @return a list of recommendations.
     */
    public List<String> generateRecommendations() {
        List<String> recommendations = new ArrayList<>();

        // Collect data from each cloud service and generate recommendations
        recommendations.addAll(getRecommendationsForProvider("IBM", ibmService));
        recommendations.addAll(getRecommendationsForProvider("GCP", gcpService));
        recommendations.addAll(getRecommendationsForProvider("AWS", awsService));
        // Add more providers as needed
        // recommendations.addAll(getRecommendationsForProvider("Azure", azureService));
        // recommendations.addAll(getRecommendationsForProvider("Oracle", oracleService));

        return recommendations;
    }

    /**
     * Generates recommendations for a single provider.
     *
     * @param providerName name of the cloud provider
     * @param service the cloud optimization service instance for the provider
     * @return a list of recommendations for the given provider.
     */
    private List<String> getRecommendationsForProvider(String providerName, CloudOptimizationService service) {
        List<String> recommendations = new ArrayList<>();

        // Step 1: Collect data from the cloud provider
        Map<String, Object> data = service.collectData();
        int totalInstances = (int) data.getOrDefault("totalInstances", 0);
        double avgCpuUtilization = (double) data.getOrDefault("avgCpuUtilization", 0.0);

        // Step 2: Make recommendations based on thresholds
        if (avgCpuUtilization < LOW_CPU_THRESHOLD && totalInstances > 1) {
            recommendations.add(providerName + ": Consider scaling down due to low CPU utilization.");
            service.terminateUnderutilizedInstance();
        } else if (avgCpuUtilization > HIGH_CPU_THRESHOLD) {
            recommendations.add(providerName + ": High CPU utilization detected. Consider scaling up.");
            service.launchNewInstance();
        } else {
            recommendations.add(providerName + ": Utilization levels are optimal.");
        }

        return recommendations;
    }

    /**
     * Provides real-time recommendations.
     *
     * @return a report containing optimization suggestions across all providers.
     */
    public Map<String, Object> getRealTimeRecommendationReport() {
        Map<String, Object> report = new HashMap<>();

        List<String> recommendations = generateRecommendations();

        report.put("timestamp", System.currentTimeMillis());
        report.put("recommendations", recommendations);

        return report;
    }
}


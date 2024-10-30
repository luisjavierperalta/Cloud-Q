package com.cloudq.cloudq.ai.service.optimization;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class GCPOptimizationService implements CloudOptimizationService {

    private static final Logger logger = LoggerFactory.getLogger(GCPOptimizationService.class);

    @Value("${gcp.project-id}")
    private String projectId;

    @Value("${gcp.zone}")
    private String zone;

    @Value("${gcp.api-key}")
    private String apiKey;

    private final HttpClient httpClient;

    public GCPOptimizationService() {
        this.httpClient = HttpClient.newHttpClient(); // Initialize the HttpClient
    }

    @Override
    public Map<String, Object> collectData() {
        Map<String, Object> resourceData = new HashMap<>();
        try {
            String url = String.format("https://compute.googleapis.com/compute/v1/projects/%s/zones/%s/instances?key=%s",
                    projectId, zone, apiKey);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                resourceData = parseInstanceData(response.body());
            } else {
                logger.error("Failed to collect data: HTTP error code " + response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Error collecting data from GCP: ", e);
        }
        return resourceData;
    }

    private Map<String, Object> parseInstanceData(String responseBody) {
        Map<String, Object> data = new HashMap<>();
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray instances = jsonResponse.optJSONArray("items");

            int totalInstances = (instances != null) ? instances.length() : 0;
            double totalCpuUtilization = 0.0;

            for (int i = 0; i < totalInstances; i++) {
                JSONObject instance = instances.getJSONObject(i);
                String instanceName = instance.getString("name");
                double cpuUtilization = getCpuUtilization(instanceName);
                totalCpuUtilization += cpuUtilization;
            }

            double avgCpuUtilization = totalInstances > 0 ? totalCpuUtilization / totalInstances : 0.0;
            data.put("totalInstances", totalInstances);
            data.put("avgCpuUtilization", avgCpuUtilization);
        } catch (Exception e) {
            logger.error("Error parsing instance data: ", e);
        }
        return data;
    }

    private double getCpuUtilization(String instanceName) {
        // Simulated CPU utilization; replace with actual metric fetching logic.
        return Math.random() * 100; // Simulated CPU utilization value
    }

    @Override
    public void optimizeResources(Map<String, Object> resourceData) {
        int totalInstances = (int) resourceData.getOrDefault("totalInstances", 0);
        double avgCpuUtilization = (double) resourceData.getOrDefault("avgCpuUtilization", 0.0);

        if (avgCpuUtilization < 30 && totalInstances > 1) {
            terminateUnderutilizedInstance();
        } else if (avgCpuUtilization > 80) {
            launchNewInstance();
        }
    }

    @Override
    public void launchNewInstance() {
        try {
            String url = String.format("https://compute.googleapis.com/compute/v1/projects/%s/zones/%s/instances?key=%s",
                    projectId, zone, apiKey);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(createInstanceBody()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                logger.info("Successfully launched new instance.");
            } else {
                logger.error("Failed to launch new instance: HTTP error code " + response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Error launching new instance: ", e);
        }
    }

    private String createInstanceBody() {
        return String.format("""
                {
                    "name": "new-instance",
                    "zone": "%s",
                    "machineType": "zones/%s/machineTypes/n1-standard-1",
                    "disks": [{
                        "boot": true,
                        "autoDelete": true,
                        "initializeParams": {
                            "sourceImage": "projects/debian-cloud/global/images/family/debian-10"
                        }
                    }],
                    "networkInterfaces": [{
                        "network": "global/networks/default",
                        "accessConfigs": [{"type": "ONE_TO_ONE_NAT", "name": "External NAT"}]
                    }]
                }
                """, zone, zone);
    }

    @Override
    public void terminateUnderutilizedInstance() {
        String instanceName = getUnderutilizedInstanceId();
        if (instanceName != null) {
            try {
                String url = String.format("https://compute.googleapis.com/compute/v1/projects/%s/zones/%s/instances/%s/stop?key=%s",
                        projectId, zone, instanceName, apiKey);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    logger.info("Successfully terminated underutilized instance: " + instanceName);
                } else {
                    logger.error("Failed to terminate instance: HTTP error code " + response.statusCode());
                }
            } catch (Exception e) {
                logger.error("Error terminating underutilized instance: ", e);
            }
        } else {
            logger.warn("No underutilized instance found to terminate.");
        }
    }

    private String getUnderutilizedInstanceId() {
        try {
            String url = String.format("https://compute.googleapis.com/compute/v1/projects/%s/zones/%s/instances?key=%s",
                    projectId, zone, apiKey);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONArray instances = new JSONObject(response.body()).optJSONArray("items");
                if (instances != null) {
                    for (int i = 0; i < instances.length(); i++) {
                        JSONObject instance = instances.getJSONObject(i);
                        String instanceName = instance.getString("name");
                        double cpuUtilization = getCpuUtilization(instanceName);

                        if (cpuUtilization < 30) {
                            return instanceName; // Return first found underutilized instance
                        }
                    }
                }
            } else {
                logger.error("Failed to retrieve instances: HTTP error code " + response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Error getting underutilized instance ID: ", e);
        }
        return null; // No underutilized instance found
    }
}
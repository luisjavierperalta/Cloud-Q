package com.cloudq.cloudq.ai.service.optimization;


import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service("IBM") // This class is a Spring Service bean
public class IBMCloudOptimizationService implements CloudOptimizationService {

    private static final Logger logger = LoggerFactory.getLogger(IBMCloudOptimizationService.class);

    private final String apiKey;
    private final String vpcId;
    private final String subnetId;
    private final String imageId;
    private final String instanceProfile;
    private final HttpClient httpClient;
    private final String authToken;

    public IBMCloudOptimizationService(
            @Value("${IBM_API_KEY}") String apiKey,
            @Value("${VPC_ID}") String vpcId,
            @Value("${SUBNET_ID}") String subnetId,
            @Value("${IMAGE_ID}") String imageId,
            @Value("${INSTANCE_PROFILE}") String instanceProfile) {

        this.apiKey = apiKey;
        this.vpcId = vpcId;
        this.subnetId = subnetId;
        this.imageId = imageId;
        this.instanceProfile = instanceProfile;

        // Validate environment variables
        if (apiKey == null || vpcId == null || subnetId == null || imageId == null || instanceProfile == null) {
            throw new IllegalStateException("One or more required environment variables are not set.");
        }

        // Set up HttpClient and authenticate with IAM to get a token
        this.httpClient = HttpClient.newHttpClient();
        this.authToken = authenticate();
    }

    private String authenticate() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://iam.cloud.ibm.com/identity/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "grant_type=urn:ibm:params:oauth:grant-type:apikey&apikey=" + apiKey))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());
            String token = jsonResponse.getString("access_token");
            logger.info("Authentication successful.");
            return token;
        } catch (Exception e) {
            logger.error("Authentication failed: ", e);
            throw new RuntimeException("Failed to authenticate");
        }
    }

    @Override
    public Map<String, Object> collectData() {
        Map<String, Object> resourceData = new HashMap<>();
        try {
            // Call the VPC API to list instances and collect data
            String uri = "https://us-south.iaas.cloud.ibm.com/v1/instances?version=2022-11-15&generation=2";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Authorization", "Bearer " + authToken)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            resourceData = parseInstanceData(response.body());
        } catch (Exception e) {
            logger.error("Error collecting data from IBM Cloud: ", e);
        }
        return resourceData;
    }

    private Map<String, Object> parseInstanceData(String responseBody) {
        Map<String, Object> data = new HashMap<>();
        JSONArray instances = new JSONObject(responseBody).getJSONArray("instances");

        int totalInstances = instances.length();
        double totalCpuUtilization = 0.0;

        // Iterate over each instance to collect CPU utilization data
        for (int i = 0; i < instances.length(); i++) {
            JSONObject instance = instances.getJSONObject(i);
            String instanceId = instance.getString("id");
            double cpuUtilization = getCpuUtilization(instanceId);
            totalCpuUtilization += cpuUtilization;
        }

        // Calculate average CPU utilization
        double avgCpuUtilization = totalInstances > 0 ? totalCpuUtilization / totalInstances : 0.0;

        // Add data to the resource map
        data.put("totalInstances", totalInstances);
        data.put("avgCpuUtilization", avgCpuUtilization);

        return data;
    }

    private double getCpuUtilization(String instanceId) {
        // Fetch CPU utilization metrics from IBM Cloud Monitoring API
        // Replace with actual monitoring API request and parsing logic if available
        return Math.random() * 100; // Simulated value for example
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
    public void terminateUnderutilizedInstance() {
        try {
            String instanceId = getUnderutilizedInstanceId(); // Get the ID of an underutilized instance
            if (instanceId != null) {
                String uri = "https://us-south.iaas.cloud.ibm.com/v1/instances/" + instanceId + "?version=2022-11-15&generation=2";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(uri))
                        .header("Authorization", "Bearer " + authToken)
                        .DELETE()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 204) {
                    logger.info("Successfully terminated instance: {}", instanceId);
                } else {
                    logger.error("Failed to terminate instance: {}", response.body());
                }
            } else {
                logger.info("No underutilized instances found for termination.");
            }
        } catch (Exception e) {
            logger.error("Error terminating instance: ", e);
        }
    }

    private String getUnderutilizedInstanceId() {
        // Real implementation: Fetch instances and find an underutilized one
        try {
            String uri = "https://us-south.iaas.cloud.ibm.com/v1/instances?version=2022-11-15&generation=2";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Authorization", "Bearer " + authToken)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray instances = new JSONObject(response.body()).getJSONArray("instances");

            for (int i = 0; i < instances.length(); i++) {
                JSONObject instance = instances.getJSONObject(i);
                String instanceId = instance.getString("id");
                double cpuUtilization = getCpuUtilization(instanceId); // Check the CPU utilization

                if (cpuUtilization < 30) { // Define a threshold for underutilization
                    return instanceId; // Return the ID of the underutilized instance
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching underutilized instance ID: ", e);
        }
        return null; // No underutilized instance found
    }

    @Override
    public void launchNewInstance() {
        try {
            String uri = "https://us-south.iaas.cloud.ibm.com/v1/instances?version=2022-11-15&generation=2";
            String requestBody = generateInstanceCreationJson();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Authorization", "Bearer " + authToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                logger.info("Successfully created instance: {}", response.body());
            } else {
                logger.error("Failed to create instance: {}", response.body());
            }
        } catch (Exception e) {
            logger.error("Error creating instance: ", e);
        }
    }

    private String generateInstanceCreationJson() {
        return String.format("""
                {
                    "name": "new-instance",
                    "profile": {"name": "%s"},
                    "vpc": {"id": "%s"},
                    "image": {"id": "%s"},
                    "primary_network_interface": {
                        "subnet": {"id": "%s"}
                    }
                }
                """, instanceProfile, vpcId, imageId, subnetId);
    }
}
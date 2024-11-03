package com.cloudq.cloudq.service;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IBMCloudProviderClient implements CloudProviderClient {

    private final String apiKey;
    private final HttpRequestFactory requestFactory;

    public IBMCloudProviderClient(String apiKey) {
        this.apiKey = apiKey;
        this.requestFactory = new NetHttpTransport().createRequestFactory();
    }

    @Override
    public void launchInstance(Map<String, String> workloadParameters) {
        String instanceName = workloadParameters.get("instanceName");
        String profile = workloadParameters.get("profile"); // e.g., "bx2-2x8"
        String imageId = workloadParameters.get("imageId"); // e.g., "rhel:7.8"

        // Set up the request body
        String requestBody = String.format(
                "{\"name\":\"%s\",\"profile\":\"%s\",\"image\":{\"id\":\"%s\"}}",
                instanceName, profile, imageId);

        sendIBMRequest("POST", "https://us-south.iaas.cloud.ibm.com/v1/instances", requestBody);
    }

    @Override
    public Map<String, Object> getMetrics() {
        // Implement metric fetching logic here, making appropriate API calls
        return new HashMap<>();
    }

    @Override
    public void stopInstance(String instanceId) {
        sendIBMRequest("POST", String.format("https://us-south.iaas.cloud.ibm.com/v1/instances/%s/action", instanceId), "{\"type\": \"stop\"}");
    }

    @Override
    public void terminateInstance(String instanceId) {
        sendIBMRequest("DELETE", String.format("https://us-south.iaas.cloud.ibm.com/v1/instances/%s", instanceId), null);
    }

    @Override
    public Map<String, Object> getInstanceDetails(String instanceId) {
        return sendIBMRequest("GET", String.format("https://us-south.iaas.cloud.ibm.com/v1/instances/%s", instanceId), null);
    }

    @Override
    public List<Map<String, Object>> listRunningInstances() {
        Map<String, Object> response = sendIBMRequest("GET", "https://us-south.iaas.cloud.ibm.com/v1/instances", null);
        return (List<Map<String, Object>>) response.get("instances");
    }

    private Map<String, Object> sendIBMRequest(String method, String endpoint, String body) {
        try {
            // Prepare the request
            var request = requestFactory.buildRequest(method, new GenericUrl(endpoint), null);

            // Set the Authorization header with API key
            request.getHeaders().set("Authorization", "Bearer " + apiKey);
            request.getHeaders().set("Content-Type", "application/json");

            // Set body if provided
            if (body != null && !body.isEmpty()) {
                request.setContent(new ByteArrayContent("application/json", body.getBytes(StandardCharsets.UTF_8)));
            }

            // Execute the request
            HttpResponse response = request.execute();
            System.out.println("Response Code: " + response.getStatusCode());

            // Handle the response and parse it into a Map
            return response.parseAs(Map.class);
        } catch (IOException e) {
            System.err.println("Error during IBM Cloud API request: " + e.getMessage());
            return new HashMap<>(); // Return empty map or handle accordingly
        }
    }
}
package com.cloudq.cloudq.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpResponse;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GcpCloudProviderClient implements CloudProviderClient {

    private final String projectId;
    private final String zone;
    private final HttpRequestFactory requestFactory;

    public GcpCloudProviderClient(String credentialsFilePath, String projectId, String zone) throws IOException {
        this.projectId = projectId;
        this.zone = zone;
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath));
        this.requestFactory = new NetHttpTransport()
                .createRequestFactory(new HttpCredentialsAdapter(credentials));
    }

    @Override
    public void launchInstance(Map<String, String> workloadParameters) {
        String instanceName = workloadParameters.get("instanceName");
        String machineType = workloadParameters.get("machineType");
        String sourceImage = workloadParameters.get("sourceImage");

        // Set up the request body
        String requestBody = String.format(
                "{\"name\":\"%s\",\"machineType\":\"zones/%s/machineTypes/%s\",\"disks\":[{\"boot\":true,\"initializeParams\":{\"sourceImage\":\"%s\"}}]}",
                instanceName, zone, machineType, sourceImage);

        sendGcpRequest("POST", String.format("https://compute.googleapis.com/compute/v1/projects/%s/zones/%s/instances", projectId, zone), requestBody);
    }

    @Override
    public Map<String, Object> getMetrics() {
        // Implement metric fetching logic here, making appropriate API calls
        return new HashMap<>();
    }

    @Override
    public void stopInstance(String instanceId) {
        sendGcpRequest("POST", String.format("https://compute.googleapis.com/compute/v1/projects/%s/zones/%s/instances/%s/stop", projectId, zone, instanceId), "");
    }

    @Override
    public void terminateInstance(String instanceId) {
        sendGcpRequest("DELETE", String.format("https://compute.googleapis.com/compute/v1/projects/%s/zones/%s/instances/%s", projectId, zone, instanceId), "");
    }

    @Override
    public Map<String, Object> getInstanceDetails(String instanceId) {
        String endpoint = String.format("https://compute.googleapis.com/compute/v1/projects/%s/zones/%s/instances/%s", projectId, zone, instanceId);
        return sendGcpRequest("GET", endpoint, null);
    }

    @Override
    public List<Map<String, Object>> listRunningInstances() {
        String endpoint = String.format("https://compute.googleapis.com/compute/v1/projects/%s/zones/%s/instances", projectId, zone);
        Map<String, Object> response = sendGcpRequest("GET", endpoint, null);
        // Parse the response to extract the instances
        return (List<Map<String, Object>>) response.get("items");
    }

    private Map<String, Object> sendGcpRequest(String method, String endpoint, String body) {
        try {
            var request = requestFactory.buildRequest(method, new GenericUrl(endpoint), null);
            if (body != null && !body.isEmpty()) {
                request.setContent(new ByteArrayContent("application/json", body.getBytes(StandardCharsets.UTF_8)));
            }

            HttpResponse response = request.execute();
            System.out.println("Response Code: " + response.getStatusCode());
            // Handle response as a Map (assuming JSON response)
            return response.parseAs(Map.class);
        } catch (IOException e) {
            System.err.println("Error during GCP API request: " + e.getMessage());
            return new HashMap<>(); // Return empty map or handle accordingly
        }
    }
}
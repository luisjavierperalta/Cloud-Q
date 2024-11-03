package com.cloudq.cloudq.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class GCPMetricsService {

    private static final String GCP_API_URL = "https://monitoring.googleapis.com/v3/projects";
    private final String accessToken;
    private final String projectId;

    // Constructor to initialize the service
    public GCPMetricsService(String credentialsFilePath, String projectId) throws Exception {
        this.projectId = projectId;
        this.accessToken = authenticateWithGCP(credentialsFilePath);
    }

    // Method to authenticate with GCP and get an access token
    private String authenticateWithGCP(String credentialsFilePath) throws IOException {
        // Load service account credentials
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath))
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        // Get access token
        credentials.refreshIfExpired(); // Refresh token if expired
        return credentials.getAccessToken().getTokenValue();
    }

    // Method to fetch metrics for a given resource ID
    public Map<String, Object> fetchMetrics(String resourceId, long startTime, long endTime) {
        Map<String, Object> metricsMap = new HashMap<>();
        try {
            // Format the URL for fetching time series data
            String url = String.format("%s/%s/timeSeries?filter=resource.type=\"gce_instance\" AND resource.labels.instance_id=\"%s\"&interval.endTime=%s&interval.startTime=%s&view=FULL",
                    GCP_API_URL, projectId, resourceId, endTime, startTime);

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch metrics: " + response.body());
            }

            // Parse the response and extract metrics
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body());
            JsonNode timeSeries = jsonNode.path("timeSeries");

            for (JsonNode series : timeSeries) {
                String metricName = series.path("metric").toString();
                metricsMap.put(metricName, series.path("points").toString()); // Customize based on the response structure
            }

        } catch (Exception e) {
            System.err.println("Error fetching metrics: " + e.getMessage());
        }

        return metricsMap;
    }

    public static void main(String[] args) {
        try {
            // Path to your GCP service account credentials JSON file
            String credentialsFilePath = "path/to/your/service-account-file.json";
            String projectId = "your-gcp-project-id"; // Replace with your actual project ID
            String resourceId = "your-instance-id"; // Replace with actual GCE instance ID

            GCPMetricsService service = new GCPMetricsService(credentialsFilePath, projectId);
            long startTime = System.currentTimeMillis() - 3600000; // 1 hour ago
            long endTime = System.currentTimeMillis(); // now
            Map<String, Object> metrics = service.fetchMetrics(resourceId, startTime, endTime);

            System.out.println("Fetched Metrics: " + metrics);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
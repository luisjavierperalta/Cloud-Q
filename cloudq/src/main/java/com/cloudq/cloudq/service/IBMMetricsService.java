package com.cloudq.cloudq.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IBMMetricsService {

    private static final String IBM_CLOUD_API_URL = "https://monitoring.cloud.ibm.com";
    private static final String API_VERSION = "v1"; // Adjust as necessary
    private final String accessToken;

    // Constructor to set the access token
    public IBMMetricsService(String apiKey) throws Exception {
        this.accessToken = authenticateWithIBM(apiKey);
    }

    // Method to authenticate with IBM Cloud and get an access token
    private String authenticateWithIBM(String apiKey) throws Exception {
        String tokenEndpoint = "https://iam.cloud.ibm.com/identity/token";
        String requestBody = "grant_type=urn:ibm:params:oauth:grant-type:apikey&apikey=" + apiKey;

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenEndpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to authenticate: " + response.body());
        }

        // Parse the response to get the access token
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.body());
        return jsonNode.get("access_token").asText();
    }

    // Method to fetch metrics for a given resource ID
    public Map<String, Object> fetchMetrics(String resourceId, long startTime, long endTime) {
        Map<String, Object> metricsMap = new HashMap<>();
        try {
            String url = String.format("%s/api/v1/metrics?resource_id=%s&start=%d&end=%d&step=60",
                    IBM_CLOUD_API_URL, resourceId, startTime, endTime);

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
            JsonNode metrics = jsonNode.path("metrics");

            for (JsonNode metric : metrics) {
                String metricName = metric.path("name").asText();
                metricsMap.put(metricName, metric.path("values").toString()); // Customize this based on response structure
            }

        } catch (Exception e) {
            System.err.println("Error fetching metrics: " + e.getMessage());
        }

        return metricsMap;
    }

    public static void main(String[] args) {
        try {
            // Replace with your IBM Cloud API key
            String apiKey = "your-ibm-cloud-api-key";
            String resourceId = "your-resource-id"; // Replace with actual resource ID

            IBMMetricsService service = new IBMMetricsService(apiKey);
            long startTime = System.currentTimeMillis() - 3600000; // 1 hour ago
            long endTime = System.currentTimeMillis(); // now
            Map<String, Object> metrics = service.fetchMetrics(resourceId, startTime, endTime);

            System.out.println("Fetched Metrics: " + metrics);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

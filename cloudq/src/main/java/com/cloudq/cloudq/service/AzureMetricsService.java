package com.cloudq.cloudq.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AzureMetricsService {

    private static final String AZURE_MONITOR_API_URL = "https://management.azure.com";
    private static final String API_VERSION = "2021-04-01"; // You can change the API version as needed
    private final String accessToken;

    // Constructor to set the access token
    public AzureMetricsService(String clientId, String clientSecret, String tenantId) throws Exception {
        this.accessToken = authenticateWithAzure(clientId, clientSecret, tenantId);
    }

    // Method to authenticate with Azure and get an access token
    private String authenticateWithAzure(String clientId, String clientSecret, String tenantId) throws Exception {
        String tokenEndpoint = String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/token", tenantId);

        String requestBody = String.format(
                "client_id=%s&client_secret=%s&grant_type=client_credentials&scope=https://management.azure.com/.default",
                clientId, clientSecret
        );

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
            String url = String.format("%s%s/metrics?api-version=%s&$filter=timeGrain eq duration'PT1M' and " +
                            "timeStamp ge datetime'%s' and timeStamp le datetime'%s'",
                    AZURE_MONITOR_API_URL, resourceId, API_VERSION,
                    Instant.ofEpochMilli(startTime).toString(),
                    Instant.ofEpochMilli(endTime).toString());

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
            JsonNode metrics = jsonNode.path("value");

            for (JsonNode metric : metrics) {
                String metricName = metric.path("name").path("localizedValue").asText();
                metricsMap.put(metricName, metric.path("timeseries").toString()); // Replace with specific processing as needed
            }

        } catch (Exception e) {
            System.err.println("Error fetching metrics: " + e.getMessage());
        }

        return metricsMap;
    }

    public static void main(String[] args) {
        try {
            // Replace with your Azure app registration details
            String clientId = "your-client-id";
            String clientSecret = "your-client-secret";
            String tenantId = "your-tenant-id";
            String resourceId = "/subscriptions/your-subscription-id/resourceGroups/your-resource-group/providers/your-resource-provider/your-resource-name"; // Replace with actual resource ID

            AzureMetricsService service = new AzureMetricsService(clientId, clientSecret, tenantId);
            long startTime = System.currentTimeMillis() - 3600000; // 1 hour ago
            long endTime = System.currentTimeMillis(); // now
            Map<String, Object> metrics = service.fetchMetrics(resourceId, startTime, endTime);

            System.out.println("Fetched Metrics: " + metrics);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.cloudq.cloudq.service;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AzureCloudProviderClient implements CloudProviderClient {

    private final String subscriptionId;
    private final String tenantId;
    private final String clientId;
    private final String clientSecret;
    private final HttpRequestFactory requestFactory;
    private String accessToken;

    public AzureCloudProviderClient(String tenantId, String clientId, String clientSecret, String subscriptionId) throws IOException {
        this.tenantId = tenantId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.subscriptionId = subscriptionId;
        this.requestFactory = new NetHttpTransport().createRequestFactory();
        this.accessToken = authenticate(); // Get the access token on initialization
    }

    @Override
    public void launchInstance(Map<String, String> workloadParameters) {
        String instanceName = workloadParameters.get("instanceName");
        String resourceGroup = workloadParameters.get("resourceGroup");
        String location = workloadParameters.get("location");
        String vmSize = workloadParameters.get("vmSize");
        String imageReference = workloadParameters.get("imageReference");

        // Set up the request body
        String requestBody = String.format(
                "{ \"location\": \"%s\", \"properties\": { \"hardwareProfile\": { \"vmSize\": \"%s\" }, \"storageProfile\": { \"imageReference\": { \"id\": \"%s\" } }, \"osProfile\": { \"computerName\": \"%s\", \"adminUsername\": \"azureuser\", \"adminPassword\": \"Password123!\" }, \"networkProfile\": { \"networkInterfaces\": [ { \"id\": \"/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/networkInterfaces/%s-nic\" } ] } } } }",
                location, vmSize, imageReference, instanceName, subscriptionId, resourceGroup, instanceName);

        sendAzureRequest("PUT", String.format("https://management.azure.com/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Compute/virtualMachines/%s?api-version=2021-03-01", subscriptionId, resourceGroup, instanceName), requestBody);
    }

    @Override
    public Map<String, Object> getMetrics() {
        // Implement metric fetching logic here, making appropriate API calls
        return new HashMap<>();
    }

    @Override
    public void stopInstance(String instanceId) {
        sendAzureRequest("POST", String.format("https://management.azure.com/subscriptions/%s/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/%s/deallocate?api-version=2021-03-01", subscriptionId, instanceId), "");
    }

    @Override
    public void terminateInstance(String instanceId) {
        sendAzureRequest("DELETE", String.format("https://management.azure.com/subscriptions/%s/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/%s?api-version=2021-03-01", subscriptionId, instanceId), "");
    }

    @Override
    public Map<String, Object> getInstanceDetails(String instanceId) {
        return sendAzureRequest("GET", String.format("https://management.azure.com/subscriptions/%s/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines/%s?api-version=2021-03-01", subscriptionId, instanceId), null);
    }

    @Override
    public List<Map<String, Object>> listRunningInstances() {
        Map<String, Object> response = sendAzureRequest("GET", String.format("https://management.azure.com/subscriptions/%s/providers/Microsoft.Compute/virtualMachines?api-version=2021-03-01", subscriptionId), null);
        return (List<Map<String, Object>>) response.get("value");
    }

    private Map<String, Object> sendAzureRequest(String method, String endpoint, String body) {
        try {
            // Prepare the request
            var request = requestFactory.buildRequest(method, new GenericUrl(endpoint), null);

            // Set the Authorization header with the Bearer token
            request.getHeaders().set("Authorization", "Bearer " + accessToken);
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
            System.err.println("Error during Azure API request: " + e.getMessage());
            return new HashMap<>(); // Return empty map or handle accordingly
        }
    }

    private String authenticate() throws IOException {
        // Use OAuth2 to get an access token from Azure AD
        String url = String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/token", tenantId);
        String requestBody = String.format("client_id=%s&client_secret=%s&grant_type=client_credentials&scope=https://management.azure.com/.default", clientId, clientSecret);

        var request = requestFactory.buildPostRequest(new GenericUrl(url), new ByteArrayContent("application/x-www-form-urlencoded", requestBody.getBytes(StandardCharsets.UTF_8)));
        HttpResponse response = request.execute();
        Map<String, Object> tokenResponse = response.parseAs(Map.class);
        return (String) tokenResponse.get("access_token"); // Return the access token
    }
}
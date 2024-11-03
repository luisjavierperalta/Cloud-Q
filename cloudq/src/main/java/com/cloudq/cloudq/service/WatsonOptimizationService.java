package com.cloudq.cloudq.service;

import com.cloudq.cloudq.model.OptimizationRequest;
import com.cloudq.cloudq.model.OptimizationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Service
public class WatsonOptimizationService {

    private final RestTemplate restTemplate;

    @Value("${watson.ml.url}") // Base URL for the Watson Machine Learning service
    private String baseUrl;

    @Value("${watson.ml.apiKey}") // API Key for authentication
    private String apiKey;

    @Autowired
    public WatsonOptimizationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Processes an optimization request and returns a response.
     *
     * @param optimizationRequest the request containing optimization details
     * @return an OptimizationResponse with details from the Watson service
     */
    public OptimizationResponse processOptimizationRequest(@Valid OptimizationRequest optimizationRequest) {
        // Prepare the request to Watson's optimization endpoint
        String url = baseUrl + "/v4/optimization"; // Adjust the endpoint as necessary
        HttpHeaders headers = createHeaders();

        ResponseEntity<OptimizationResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(optimizationRequest, headers),
                OptimizationResponse.class
        );

        return response.getBody();
    }

    /**
     * Fetches metrics based on a resource ID.
     *
     * @param resourceId the resource ID to fetch metrics for
     * @return a map containing key-value pairs of metrics data
     */
    public Map<String, Object> fetchMetrics(String resourceId) {
        String url = baseUrl + "/v4/metrics/" + resourceId; // Adjust based on the actual API endpoint
        HttpHeaders headers = createHeaders();

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        // Return the response body as a Map
        return response.getBody();
    }

    /**
     * Get model details using the model ID.
     *
     * @param modelId the ID of the model to retrieve details for
     * @return model details as a JSON string
     */
    public String getModelDetails(String modelId) {
        String url = baseUrl + "/v4/models/" + modelId; // Adjust based on the actual API endpoint
        HttpHeaders headers = createHeaders();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        return response.getBody();
    }

    /**
     * Score a model with input data.
     *
     * @param modelId      the ID of the model to score
     * @param deploymentId the ID of the deployment
     * @param inputData    the input data for scoring
     * @return the scoring result as a JSON string
     */
    public String scoreModel(String modelId, String deploymentId, Map<String, Object> inputData) {
        String url = baseUrl + "/v4/models/" + modelId + "/deployments/" + deploymentId + "/score";
        HttpHeaders headers = createHeaders();

        // Creating the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("input_data", inputData);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                String.class
        );

        return response.getBody();
    }

    /**
     * Creates the headers for the HTTP requests, including the Authorization header.
     *
     * @return HttpHeaders with necessary information
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        return headers;
    }
}

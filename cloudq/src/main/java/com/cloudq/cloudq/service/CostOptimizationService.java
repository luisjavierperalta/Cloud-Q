package com.cloudq.cloudq.service;

import com.cloudq.cloudq.model.OptimizationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CostOptimizationService {

    private final RestTemplate restTemplate;

    @Value("${watson.ml.url}") // Base URL for Watson Machine Learning API
    private String baseUrl;

    @Value("${watson.ml.apiKey}") // API Key for Watson Machine Learning
    private String apiKey;

    @Autowired
    public CostOptimizationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Optimize costs based on the given parameters.
     *
     * @param optimizationRequest the request containing the optimization parameters
     * @return the optimization results as a JSON string
     */
    public String optimizeCosts(OptimizationRequest optimizationRequest) {
        String url = baseUrl + "/v4/models/optimization_model_id/optimize"; // Replace with the actual optimization endpoint
        HttpHeaders headers = createHeaders();

        // Create the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("parameters", optimizationRequest.getParameters()); // Assuming `parameters` is a field in OptimizationRequest

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class);
            return response.getBody(); // Return the optimization result as a string
        } catch (HttpClientErrorException e) {
            // Handle client-side errors (4xx)
            System.err.println("Client error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Error while optimizing costs: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            // Handle server-side errors (5xx)
            System.err.println("Server error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Error while optimizing costs: " + e.getMessage(), e);
        } catch (RestClientException e) {
            // Handle other RestTemplate exceptions (network issues, etc.)
            System.err.println("Network error: " + e.getMessage());
            throw new RuntimeException("Error while optimizing costs: " + e.getMessage(), e);
        }
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

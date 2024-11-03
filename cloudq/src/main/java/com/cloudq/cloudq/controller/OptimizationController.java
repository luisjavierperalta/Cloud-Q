package com.cloudq.cloudq.controller;

import com.cloudq.cloudq.model.OptimizationRequest;
import com.cloudq.cloudq.model.OptimizationResponse;
import com.cloudq.cloudq.service.WatsonOptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/optimization")
public class OptimizationController {

    private final WatsonOptimizationService optimizationservice;


    @Autowired
    public OptimizationController(WatsonOptimizationService optimizationService, WatsonOptimizationService optimizationservice) {
        this.optimizationservice = optimizationservice;

    }

    /**
     * Create a new optimization request.
     *
     * @param optimizationRequest The optimization request object.
     * @return Response entity with the created optimization request details.
     */
    @PostMapping
    public ResponseEntity<OptimizationResponse> createOptimizationRequest(@Valid @RequestBody OptimizationRequest optimizationRequest) {
        try {
            OptimizationResponse response = WatsonOptimizationService.processOptimizationRequest(optimizationRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log the error (optional)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get the metrics for a specific resource ID.
     *
     * @param resourceId The ID of the resource for which metrics are requested.
     * @return Response entity containing the metrics for the specified resource.
     */
    @GetMapping("/metrics/{resourceId}")
    public ResponseEntity<Map<String, Object>> getMetrics(@PathVariable String resourceId) {
        try {
            Map<String, Object> metrics = WatsonOptimizationService.fetchMetrics(resourceId);
            if (metrics.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(metrics, HttpStatus.OK);
        } catch (Exception e) {
            // Log the error (optional)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
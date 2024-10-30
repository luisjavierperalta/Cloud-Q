package com.cloudq.cloudq.ai.model;

import java.util.HashMap;
import java.util.Map;

public class ResourceAllocationModel {

    public ResourceAllocationOutput allocateResources(ResourceAllocationInput input) {
        Map<String, Integer> resourceRequests = input.getResourceRequests();
        Map<String, Integer> availableResources = input.getAvailableResources();

        Map<String, Integer> allocatedResources = new HashMap<>();
        Map<String, Integer> unmetRequests = new HashMap<>();

        // Iterate over each resource type requested
        for (String resourceType : resourceRequests.keySet()) {
            int requestedAmount = resourceRequests.get(resourceType);
            int availableAmount = availableResources.getOrDefault(resourceType, 0);

            // Allocate resources based on availability
            if (availableAmount >= requestedAmount) {
                allocatedResources.put(resourceType, requestedAmount);
                unmetRequests.put(resourceType, 0); // No unmet requests
            } else {
                allocatedResources.put(resourceType, availableAmount);
                unmetRequests.put(resourceType, requestedAmount - availableAmount); // Calculate unmet requests
            }
        }

        // Return the output containing allocated resources and unmet requests
        return new ResourceAllocationOutput(allocatedResources, unmetRequests);
    }
}

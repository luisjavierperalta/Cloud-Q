package com.cloudq.cloudq.ai.model;

import java.util.Map;

public class ResourceAllocationInput {
    private Map<String, Integer> resourceRequests; // Key: resource type, Value: requested amount
    private Map<String, Integer> availableResources; // Key: resource type, Value: available amount

    public ResourceAllocationInput(Map<String, Integer> resourceRequests, Map<String, Integer> availableResources) {
        this.resourceRequests = resourceRequests;
        this.availableResources = availableResources;
    }

    public Map<String, Integer> getResourceRequests() {
        return resourceRequests;
    }

    public Map<String, Integer> getAvailableResources() {
        return availableResources;
    }
}


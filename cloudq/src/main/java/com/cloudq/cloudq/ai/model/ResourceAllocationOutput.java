package com.cloudq.cloudq.ai.model;

import java.util.Map;

public class ResourceAllocationOutput {
    private Map<String, Integer> allocatedResources; // Key: resource type, Value: allocated amount
    private Map<String, Integer> unmetRequests; // Key: resource type, Value: unmet amount

    public ResourceAllocationOutput(Map<String, Integer> allocatedResources, Map<String, Integer> unmetRequests) {
        this.allocatedResources = allocatedResources;
        this.unmetRequests = unmetRequests;
    }

    public Map<String, Integer> getAllocatedResources() {
        return allocatedResources;
    }

    public Map<String, Integer> getUnmetRequests() {
        return unmetRequests;
    }

    @Override
    public String toString() {
        return "ResourceAllocationOutput{" +
                "allocatedResources=" + allocatedResources +
                ", unmetRequests=" + unmetRequests +
                '}';
    }
}

package com.cloudq.cloudq.ai.model;

import java.util.HashMap;
import java.util.Map;

public class ResourceCost {
    private Map<String, Double> costs;

    public ResourceCost() {
        this.costs = new HashMap<>();
    }

    public void setCost(String resourceType, double cost) {
        costs.put(resourceType, cost);
    }

    public double getCost(String resourceType) {
        return costs.getOrDefault(resourceType, 0.0);
    }
}

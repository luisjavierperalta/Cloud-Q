package com.cloudq.cloudq.ai.model;

public class CostEstimationModel {
    private ResourceCost resourceCost;

    public CostEstimationModel(ResourceCost resourceCost) {
        this.resourceCost = resourceCost;
    }

    public double estimateCosts(double[][] resourceUsage) {
        double totalCost = 0.0;

        // Assume resourceUsage is a 2D array where:
        // Each row corresponds to a different resource type
        // Each column corresponds to the usage amount for that resource type
        for (int i = 0; i < resourceUsage.length; i++) {
            String resourceType = "ResourceType" + (i + 1); // Assuming resource types are named
            double usage = resourceUsage[i][0]; // Assuming the first column has the usage amount

            double costPerUnit = resourceCost.getCost(resourceType);
            totalCost += usage * costPerUnit; // Calculate total cost for this resource type
        }

        return totalCost;
    }
}

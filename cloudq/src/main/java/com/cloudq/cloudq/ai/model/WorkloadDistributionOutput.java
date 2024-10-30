package com.cloudq.cloudq.ai.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkloadDistributionOutput {
    private Map<String, String> allocations; // Maps resource ID to allocated workload
    private List<String> unallocatedWorkloads;

    public WorkloadDistributionOutput() {
        this.allocations = new HashMap<>();
        this.unallocatedWorkloads = new ArrayList<>();
    }

    public void addAllocation(String resourceId, String workloadName) {
        allocations.put(resourceId, workloadName);
    }

    public void addUnallocatedWorkload(String workloadName) {
        unallocatedWorkloads.add(workloadName);
    }

    public Map<String, String> getAllocations() {
        return allocations;
    }

    public List<String> getUnallocatedWorkloads() {
        return unallocatedWorkloads;
    }
}


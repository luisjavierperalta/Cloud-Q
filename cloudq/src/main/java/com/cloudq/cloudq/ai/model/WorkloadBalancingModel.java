package com.cloudq.cloudq.ai.model;

import java.util.ArrayList;
import java.util.List;

public class WorkloadBalancingModel {

    public WorkloadDistributionOutput balanceWorkloads(WorkloadDistributionInput input) {
        List<Workload> workloads = input.getWorkloads();
        List<Resource> resources = input.getResources();
        WorkloadDistributionOutput output = new WorkloadDistributionOutput();

        for (Workload workload : workloads) {
            Resource bestResource = findBestResource(resources, workload);
            if (bestResource != null) {
                bestResource.allocate(workload);
                output.addAllocation(bestResource.getId(), workload.getName());
            } else {
                output.addUnallocatedWorkload(workload.getName());
            }
        }

        return output;
    }

    private Resource findBestResource(List<Resource> resources, Workload workload) {
        Resource bestResource = null;
        for (Resource resource : resources) {
            if (resource.canAllocate(workload)) {
                // Choose the resource with the least current CPU usage as a simple strategy
                if (bestResource == null || resource.getCurrentCpuUsage() < bestResource.getCurrentCpuUsage()) {
                    bestResource = resource;
                }
            }
        }
        return bestResource;
    }
}

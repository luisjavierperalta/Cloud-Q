package com.cloudq.cloudq.ai.model;

import java.util.List;

public class WorkloadDistributionInput {
    private List<Workload> workloads;
    private List<Resource> resources;

    public WorkloadDistributionInput(List<Workload> workloads, List<Resource> resources) {
        this.workloads = workloads;
        this.resources = resources;
    }

    public List<Workload> getWorkloads() {
        return workloads;
    }

    public List<Resource> getResources() {
        return resources;
    }
}

class Workload {
    private String name;
    private int cpuRequirement;
    private int memoryRequirement;

    public Workload(String name, int cpuRequirement, int memoryRequirement) {
        this.name = name;
        this.cpuRequirement = cpuRequirement;
        this.memoryRequirement = memoryRequirement;
    }

    public String getName() {
        return name;
    }

    public int getCpuRequirement() {
        return cpuRequirement;
    }

    public int getMemoryRequirement() {
        return memoryRequirement;
    }
}

class Resource {
    private String id;
    private int cpuCapacity;
    private int memoryCapacity;
    private int currentCpuUsage = 0;
    private int currentMemoryUsage = 0;

    public Resource(String id, int cpuCapacity, int memoryCapacity) {
        this.id = id;
        this.cpuCapacity = cpuCapacity;
        this.memoryCapacity = memoryCapacity;
    }

    public String getId() {
        return id;
    }

    public int getCpuCapacity() {
        return cpuCapacity;
    }

    public int getMemoryCapacity() {
        return memoryCapacity;
    }

    public int getCurrentCpuUsage() {
        return currentCpuUsage;
    }

    public int getCurrentMemoryUsage() {
        return currentMemoryUsage;
    }

    public boolean canAllocate(Workload workload) {
        return (currentCpuUsage + workload.getCpuRequirement() <= cpuCapacity) &&
                (currentMemoryUsage + workload.getMemoryRequirement() <= memoryCapacity);
    }

    public void allocate(Workload workload) {
        currentCpuUsage += workload.getCpuRequirement();
        currentMemoryUsage += workload.getMemoryRequirement();
    }
}

package com.cloudq.cloudq.ai.service.optimization;

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AWSCloudOptimizationService {

    private static final Logger logger = LoggerFactory.getLogger(AWSCloudOptimizationService.class);

    private final Ec2Client ec2Client;
    private final CloudWatchClient cloudWatchClient;
    private final AutoScalingClient autoScalingClient;

    // Assuming environment variables are set for the following
    private final String amiId = System.getenv("AMI_ID");
    private final String instanceType = System.getenv("INSTANCE_TYPE");
    private final String subnetId = System.getenv("SUBNET_ID");

    public AWSCloudOptimizationService() {
        this.ec2Client = Ec2Client.create();
        this.cloudWatchClient = CloudWatchClient.create();
        this.autoScalingClient = AutoScalingClient.create();
    }

    public Map<String, Object> collectData() {
        Map<String, Object> resourceData = new HashMap<>();
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();

        try {
            DescribeInstancesResponse response = ec2Client.describeInstances(request);

            int totalInstances = 0;
            double totalCpuUtilization = 0;

            for (Reservation reservation : response.reservations()) {
                for (software.amazon.awssdk.services.ec2.model.Instance instance : reservation.instances()) {
                    totalInstances++;
                    double cpuUtilization = getCpuUtilization(instance.instanceId());
                    totalCpuUtilization += cpuUtilization;
                }
            }

            resourceData.put("totalInstances", totalInstances);
            resourceData.put("avgCpuUtilization", totalInstances > 0 ? totalCpuUtilization / totalInstances : 0);
        } catch (Exception e) {
            logger.error("Failed to collect instance data: {}", e.getMessage());
        }

        return resourceData;
    }

    private double getCpuUtilization(String instanceId) {
        Instant endTime = Instant.now();
        Instant startTime = endTime.minusSeconds(3600); // Last hour

        GetMetricStatisticsRequest request = GetMetricStatisticsRequest.builder()
                .metricName("CPUUtilization")
                .startTime(startTime)
                .endTime(endTime)
                .period(300) // 5-minute intervals
                .namespace("AWS/EC2")
                .statisticsWithStrings("Average")
                .dimensions(Dimension.builder().name("InstanceId").value(instanceId).build())
                .build();

        try {
            GetMetricStatisticsResponse result = cloudWatchClient.getMetricStatistics(request);
            return result.datapoints().stream()
                    .mapToDouble(Datapoint::average)
                    .average()
                    .orElse(0.0);
        } catch (Exception e) {
            logger.error("Failed to retrieve CPU utilization for instance {}: {}", instanceId, e.getMessage());
            return 0.0; // Return a default value in case of failure
        }
    }

    public void optimizeResources(Map<String, Object> awsData) {
        int totalInstances = (int) awsData.get("totalInstances");
        double avgCpuUtilization = (double) awsData.get("avgCpuUtilization");

        // Example optimization logic
        if (avgCpuUtilization < 20 && totalInstances > 1) {
            terminateUnderutilizedInstance();
        } else if (avgCpuUtilization > 70) {
            launchNewInstance();
        }
    }

    private void terminateUnderutilizedInstance() {
        try {
            // Get the list of instances
            DescribeInstancesResponse describeResponse = ec2Client.describeInstances(DescribeInstancesRequest.builder().build());
            Optional<software.amazon.awssdk.services.ec2.model.Instance> underutilizedInstance = describeResponse.reservations().stream()
                    .flatMap(reservation -> reservation.instances().stream())
                    .filter(instance -> {
                        double cpuUtilization = getCpuUtilization(instance.instanceId());
                        return cpuUtilization < 20; // Criteria for underutilization
                    })
                    .findFirst();

            if (underutilizedInstance.isPresent()) {
                String instanceId = underutilizedInstance.get().instanceId();
                // Terminate the selected underutilized instance
                ec2Client.terminateInstances(TerminateInstancesRequest.builder()
                        .instanceIds(instanceId)
                        .build());
                logger.info("Terminated underutilized instance: {}", instanceId);
            } else {
                logger.info("No underutilized instances found for termination.");
            }
        } catch (Exception e) {
            logger.error("Failed to terminate underutilized instance: {}", e.getMessage());
        }
    }

    private void launchNewInstance() {
        try {
            // Launch a new EC2 instance
            RunInstancesRequest runInstancesRequest = RunInstancesRequest.builder()
                    .imageId(amiId)
                    .instanceType(instanceType)
                    .subnetId(subnetId)
                    .minCount(1)
                    .maxCount(1)
                    .build();

            RunInstancesResponse runInstancesResponse = ec2Client.runInstances(runInstancesRequest);
            String newInstanceId = runInstancesResponse.instances().get(0).instanceId();
            logger.info("Launched new instance: {}", newInstanceId);
        } catch (Exception e) {
            logger.error("Failed to launch new instance: {}", e.getMessage());
        }
    }
}

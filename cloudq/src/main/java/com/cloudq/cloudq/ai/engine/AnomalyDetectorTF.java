package com.cloudq.cloudq.ai.engine;

import  java.util.ArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AnomalyDetectorTF {
    private int numClusters;
    private List<double[]> centroids;

    public AnomalyDetectorTF(int numClusters) {
        this.numClusters = numClusters;
        this.centroids = new ArrayList<>();
    }

    // Method to train the K-Means model
    public void train(double[][] resourceUsageData) {
        // Initialize centroids randomly from the data points
        initializeCentroids(resourceUsageData);

        boolean centroidsChanged;
        do {
            // Step 1: Assign data points to the nearest centroid
            List<List<double[]>> clusters = assignClusters(resourceUsageData);

            // Step 2: Update centroids
            centroidsChanged = updateCentroids(clusters);
        } while (centroidsChanged);
    }

    // Method to detect anomalies in new resource usage data
    public int[] detectAnomalies(double[][] newResourceUsageData) {
        int[] anomalies = new int[newResourceUsageData.length];

        for (int i = 0; i < newResourceUsageData.length; i++) {
            double minDistance = Double.MAX_VALUE;
            for (double[] centroid : centroids) {
                double distance = calculateDistance(newResourceUsageData[i], centroid);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }

            // Threshold check to classify as anomaly
            if (minDistance > 1.0) { // Example threshold
                anomalies[i] = 1; // Mark as anomaly
            } else {
                anomalies[i] = 0; // Not an anomaly
            }
        }

        return anomalies;
    }

    // Calculate the distance between two points
    private double calculateDistance(double[] pointA, double[] pointB) {
        double sum = 0.0;
        for (int i = 0; i < pointA.length; i++) {
            sum += Math.pow(pointA[i] - pointB[i], 2);
        }
        return Math.sqrt(sum);
    }

    // Initialize centroids by randomly selecting data points
    private void initializeCentroids(double[][] resourceUsageData) {
        Random random = new Random();
        for (int i = 0; i < numClusters; i++) {
            centroids.add(resourceUsageData[random.nextInt(resourceUsageData.length)]);
        }
    }

    // Assign data points to the nearest centroids
    private List<List<double[]>> assignClusters(double[][] data) {
        List<List<double[]>> clusters = new ArrayList<>(numClusters);
        for (int i = 0; i < numClusters; i++) {
            clusters.add(new ArrayList<>());
        }

        for (double[] dataPoint : data) {
            double minDistance = Double.MAX_VALUE;
            int closestCentroidIndex = -1;

            for (int i = 0; i < centroids.size(); i++) {
                double distance = calculateDistance(dataPoint, centroids.get(i));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestCentroidIndex = i;
                }
            }
            clusters.get(closestCentroidIndex).add(dataPoint);
        }

        return clusters;
    }

    // Update centroids based on assigned clusters
    private boolean updateCentroids(List<List<double[]>> clusters) {
        boolean centroidsChanged = false;

        for (int i = 0; i < numClusters; i++) {
            if (clusters.get(i).isEmpty()) continue; // No points assigned to this cluster

            double[] newCentroid = new double[centroids.get(i).length];
            for (double[] point : clusters.get(i)) {
                for (int j = 0; j < point.length; j++) {
                    newCentroid[j] += point[j];
                }
            }
            for (int j = 0; j < newCentroid.length; j++) {
                newCentroid[j] /= clusters.get(i).size(); // Average the points
            }

            // Check if centroid has changed
            if (!Arrays.equals(newCentroid, centroids.get(i))) {
                centroids.set(i, newCentroid);
                centroidsChanged = true;
            }
        }

        return centroidsChanged;
    }
}
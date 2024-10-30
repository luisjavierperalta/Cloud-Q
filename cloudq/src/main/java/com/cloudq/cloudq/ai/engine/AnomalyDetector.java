package com.cloudq.cloudq.ai.engine;


import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.RowFactory;

import java.util.Arrays;

public class AnomalyDetector {
    private SparkSession spark;
    private KMeansModel kMeansModel;
    private int numClusters;

    public AnomalyDetector(SparkSession spark, int numClusters) {
        this.spark = spark;
        this.numClusters = numClusters; // Number of clusters for K-Means
    }

    // Method to train the K-Means model
    public void train(double[][] resourceUsageData) {
        // Define schema for DataFrame
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert 2D array to Rows
        Row[] rows = new Row[resourceUsageData.length];
        for (int i = 0; i < resourceUsageData.length; i++) {
            rows[i] = RowFactory.create(Vectors.dense(resourceUsageData[i]));
        }

        // Create DataFrame
        Dataset<Row> trainingData = spark.createDataFrame(Arrays.asList(rows), schema);

        // Train K-Means model
        KMeans kMeans = new KMeans().setK(numClusters).setSeed(1L);
        kMeansModel = kMeans.fit(trainingData);
    }

    // Method to detect anomalies in new resource usage data
    public int[] detectAnomalies(double[][] newResourceUsageData) {
        // Define schema for prediction DataFrame
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert new data to Rows
        Row[] rows = new Row[newResourceUsageData.length];
        for (int i = 0; i < newResourceUsageData.length; i++) {
            rows[i] = RowFactory.create(Vectors.dense(newResourceUsageData[i]));
        }

        // Create DataFrame for predictions
        Dataset<Row> predictionData = spark.createDataFrame(Arrays.asList(rows), schema);

        // Make predictions using the trained model
        Dataset<Row> predictions = kMeansModel.transform(predictionData);

        // Extract prediction results (cluster assignments) and convert to int[]
        return predictions.select("prediction")
                .as(org.apache.spark.sql.Encoders.INT())
                .collectAsList()
                .stream()
                .mapToInt(row -> row) // Correctly extract the integer prediction
                .toArray();
    }

    // Optional: Method to evaluate if the data is anomalous based on distance from cluster centroids
    public boolean isAnomaly(double[] dataPoint, double threshold) {
        // Create a DataFrame for the single data point
        Dataset<Row> singleDataPoint = spark.createDataFrame(
                Arrays.asList(RowFactory.create(Vectors.dense(dataPoint))),
                DataTypes.createStructType(new StructField[]{
                        DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
                })
        );

        // Get the predicted cluster for the data point
        Row prediction = kMeansModel.transform(singleDataPoint).select("prediction").first();
        int clusterIndex = (int) prediction.get(0); // Use get(0) to get the integer value from Row

        // Calculate the distance from the centroid of the predicted cluster
        double[] centroid = kMeansModel.clusterCenters()[clusterIndex].toArray();
        double distance = Vectors.sqdist(Vectors.dense(dataPoint), Vectors.dense(centroid));

        // If the distance exceeds the threshold, classify as an anomaly
        return distance > threshold;
    }
}



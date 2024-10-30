package com.cloudq.cloudq.ai.model;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.Encoders;

import java.util.Arrays;
import java.util.List;

public class AnomalyDetectionModel {
    private SparkSession spark;
    private KMeansModel kMeansModel; // Store the trained KMeans model

    // Constructor
    public AnomalyDetectionModel(SparkSession spark) {
        this.spark = spark;
        this.kMeansModel = null; // Initialize with no model
    }

    // Method to train the KMeans model
    public void train(double[][] data) {
        // Define the schema for the DataFrame
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert data into Rows
        Row[] rows = new Row[data.length];
        for (int i = 0; i < data.length; i++) {
            // Create a dense vector for features
            rows[i] = RowFactory.create(Vectors.dense(data[i]));
        }

        // Create DataFrame from Rows
        Dataset<Row> trainingData = spark.createDataFrame(Arrays.asList(rows), schema);

        // Train the KMeans model
        KMeans kMeans = new KMeans().setK(2).setSeed(1L); // Set K (number of clusters) and seed
        kMeansModel = kMeans.fit(trainingData); // Fit the model and store it
    }

    // Method to predict cluster assignments for new data
    public Double[] predict(double[][] data) {
        // Check if the model is trained
        if (kMeansModel == null) {
            throw new IllegalStateException("Model has not been trained yet.");
        }

        // Define the schema for the DataFrame
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert data into Rows
        Row[] rows = new Row[data.length];
        for (int i = 0; i < data.length; i++) {
            rows[i] = RowFactory.create(Vectors.dense(data[i]));
        }

        // Create DataFrame from Rows
        Dataset<Row> predictionData = spark.createDataFrame(Arrays.asList(rows), schema);

        // Perform prediction using the trained model
        Dataset<Row> predictions = kMeansModel.transform(predictionData); // Use KMeansModel to transform

        // Extract predictions
        List<Double> predictionList = predictions.select("prediction").as(Encoders.DOUBLE()).collectAsList();
        Double[] results = new Double[predictionList.size()];
        predictionList.toArray(results); // Convert List to array

        return results;
    }

    // Method for evaluating the model (to be implemented if needed)
    public void evaluate(Dataset<Row> testData) {
        // Add evaluation logic if necessary
        // This could include metrics such as silhouette score, etc.
    }

    // Getter for KMeansModel (optional, if you need to access it outside)
    public KMeansModel getKMeansModel() {
        return kMeansModel;
    }
}
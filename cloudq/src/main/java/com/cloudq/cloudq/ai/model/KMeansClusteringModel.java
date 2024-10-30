package com.cloudq.cloudq.ai.model;

import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.evaluation.ClusteringEvaluator;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;

public class KMeansClusteringModel extends AIModel {
    private KMeansModel trainedModel;
    private final KMeans model;
    private final SparkSession spark;

    public KMeansClusteringModel(SparkSession spark) {
        this.spark = spark;
        this.model = new KMeans().setK(3).setSeed(1L); // Configure number of clusters (e.g., 3)
    }

    @Override
    public void train(double[][] data, double[] labels) {
        Dataset<Row> trainingData = createDataFrame(data);
        this.trainedModel = model.fit(trainingData);
    }

    @Override
    public double[] predict(double[][] data) {
        Dataset<Row> testData = createDataFrame(data);
        Dataset<Row> predictions = trainedModel.transform(testData);
        List<Double> predictionList = predictions.select("prediction").as(Encoders.DOUBLE()).collectAsList();

        // Convert predictions list to array
        double[] predictionArray = new double[predictionList.size()];
        for (int i = 0; i < predictionList.size(); i++) {
            predictionArray[i] = predictionList.get(i);
        }
        return predictionArray;
    }

    @Override
    public ModelMetrics evaluate(double[][] testData, double[] testLabels) {
        Dataset<Row> testDataFrame = createDataFrame(testData);
        Dataset<Row> predictions = trainedModel.transform(testDataFrame);

        ClusteringEvaluator evaluator = new ClusteringEvaluator();
        double silhouette = evaluator.evaluate(predictions);

        // ModelMetrics accepts (accuracy, precision, recall, f1, silhouette)
        return new ModelMetrics(silhouette, silhouette, silhouette, silhouette, silhouette);
    }

    private Dataset<Row> createDataFrame(double[][] data) {
        // Define schema for DataFrame with a single column 'features' for clustering
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert 2D array data to Rows
        List<Row> rows = new ArrayList<>();
        for (double[] rowData : data) {
            rows.add(RowFactory.create(Vectors.dense(rowData)));
        }

        // Create DataFrame from rows
        return spark.createDataFrame(rows, schema);
    }
}

package com.cloudq.cloudq.ai.model;

import org.apache.spark.sql.*;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;

public class TimeSeriesForecastingModel extends AIModel {
    private LinearRegression model;
    private SparkSession spark;

    public TimeSeriesForecastingModel(SparkSession spark) {
        this.spark = spark;
        this.model = new LinearRegression().setLabelCol("label").setFeaturesCol("features");
    }

    @Override
    public void train(double[][] data, double[] labels) {
        // Define schema for training data
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("label", DataTypes.DoubleType, false),
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert data into Rows
        Row[] rows = new Row[data.length];
        for (int i = 0; i < data.length; i++) {
            org.apache.spark.ml.linalg.Vector featureVector = Vectors.dense(data[i]);
            rows[i] = RowFactory.create(labels[i], featureVector);
        }

        // Create DataFrame
        Dataset<Row> trainingData = spark.createDataFrame(Arrays.asList(rows), schema);

        // Train the model
        model.fit(trainingData);
    }

    @Override
    public double[] predict(double[][] data) {
        // Define schema for input data
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert input data into Rows
        Row[] rows = new Row[data.length];
        for (int i = 0; i < data.length; i++) {
            org.apache.spark.ml.linalg.Vector featureVector = Vectors.dense(data[i]);
            rows[i] = RowFactory.create(featureVector);
        }

        // Create DataFrame for prediction
        Dataset<Row> predictionData = spark.createDataFrame(Arrays.asList(rows), schema);

        // Make predictions
        Dataset<Row> predictions = model.fit(predictionData).transform(predictionData);

        // Extract predictions and convert to double array
        Double[] result = predictions.select("prediction").as(Encoders.DOUBLE()).collect();
        return result;
    }

    @Override
    public ModelMetrics evaluate(double[][] testData, double[] testLabels) {
        // Define schema for test data
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("label", DataTypes.DoubleType, false),
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert test data into Rows
        Row[] rows = new Row[testData.length];
        for (int i = 0; i < testData.length; i++) {
            org.apache.spark.ml.linalg.Vector featureVector = Vectors.dense(testData[i]);
            rows[i] = RowFactory.create(testLabels[i], featureVector);
        }

        // Create DataFrame for testing
        Dataset<Row> testDataFrame = spark.createDataFrame(Arrays.asList(rows), schema);

        // Make predictions
        Dataset<Row> predictions = model.fit(testDataFrame).transform(testDataFrame);

        // Evaluate the model's performance
        RegressionEvaluator evaluator = new RegressionEvaluator()
                .setLabelCol("label")
                .setPredictionCol("prediction");

        double rmse = evaluator.setMetricName("rmse").evaluate(predictions);
        double r2 = evaluator.setMetricName("r2").evaluate(predictions);

        // Assuming ModelMetrics constructor takes RMSE and R² values
        return new ModelMetrics(rmse, r2);
    }
}

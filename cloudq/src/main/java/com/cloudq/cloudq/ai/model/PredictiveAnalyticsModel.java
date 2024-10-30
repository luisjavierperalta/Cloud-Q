package com.cloudq.cloudq.ai.model;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.ml.linalg.Vectors;

import java.util.Arrays;

public class PredictiveAnalyticsModel extends AIModel {
    private SparkSession spark;
    private LinearRegression model;
    private org.apache.spark.ml.regression.LinearRegressionModel trainedModel; // Trained model

    public PredictiveAnalyticsModel(SparkSession spark) {
        this.spark = spark;
        this.model = new LinearRegression();
    }

    @Override
    public void train(double[][] data, double[] labels) {
        // Define schema for the DataFrame
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("label", DataTypes.DoubleType, false),
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert the data to Row format
        Row[] rows = new Row[data.length];
        for (int i = 0; i < data.length; i++) {
            org.apache.spark.ml.linalg.Vector featureVector = Vectors.dense(data[i]);
            rows[i] = RowFactory.create(labels[i], featureVector);
        }

        // Create DataFrame
        Dataset<Row> trainingData = spark.createDataFrame(Arrays.asList(rows), schema);

        // Fit the model and save the trained model
        trainedModel = model.fit(trainingData); // Store the trained model
    }

    @Override
    public double[] predict(double[][] data) {
        // Define schema for the DataFrame
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert the data to Row format
        Row[] rows = new Row[data.length];
        for (int i = 0; i < data.length; i++) {
            org.apache.spark.ml.linalg.Vector featureVector = Vectors.dense(data[i]);
            rows[i] = RowFactory.create(featureVector);
        }

        // Create DataFrame for prediction
        Dataset<Row> predictionData = spark.createDataFrame(Arrays.asList(rows), schema);

        // Generate predictions using the trained model
        Dataset<Row> predictions = trainedModel.transform(predictionData);

        // Collect predictions
        double[] predictionResults = new double[(int) predictions.count()];
        for (int i = 0; i < predictionResults.length; i++) {
            predictionResults[i] = predictions.collectAsList().get(i).getDouble(0); // Access the prediction column
        }

        return predictionResults;
    }

    @Override
    public ModelMetrics evaluate(double[][] testData, double[] testLabels) {
        // Define schema for the test DataFrame
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("label", DataTypes.DoubleType, false),
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert the test data to Row format
        Row[] rows = new Row[testData.length];
        for (int i = 0; i < testData.length; i++) {
            org.apache.spark.ml.linalg.Vector featureVector = Vectors.dense(testData[i]);
            rows[i] = RowFactory.create(testLabels[i], featureVector);
        }

        // Create DataFrame for testing
        Dataset<Row> testDataFrame = spark.createDataFrame(Arrays.asList(rows), schema);

        // Generate predictions on test data
        Dataset<Row> predictions = trainedModel.transform(testDataFrame);

        // Evaluate the model
        RegressionEvaluator evaluator = new RegressionEvaluator()
                .setLabelCol("label")
                .setPredictionCol("prediction");

        double mse = evaluator.setMetricName("mse").evaluate(predictions);
        double rmse = Math.sqrt(mse); // Root Mean Squared Error

        // Assuming we want to return ModelMetrics with just RMSE for regression
        return new ModelMetrics(rmse, 0, 0, 0, 0); // Precision, recall, fMeasure are not applicable for regression
    }
}

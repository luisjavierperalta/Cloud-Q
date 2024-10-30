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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PredictiveAnalysisModel extends AIModel {
    private SparkSession spark;
    private LinearRegression model;
    private org.apache.spark.ml.regression.LinearRegressionModel trainedModel; // Trained model

    public PredictiveAnalysisModel(SparkSession spark) {
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
        List<Row> rows = IntStream.range(0, data.length)
                .mapToObj(i -> RowFactory.create(labels[i], Vectors.dense(data[i])))
                .collect(Collectors.toList());

        // Create DataFrame
        Dataset<Row> trainingData = spark.createDataFrame(rows, schema);

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
        List<Row> rows = IntStream.range(0, data.length)
                .mapToObj(i -> RowFactory.create(Vectors.dense(data[i])))
                .collect(Collectors.toList());

        // Create DataFrame for prediction
        Dataset<Row> predictionData = spark.createDataFrame(rows, schema);

        // Generate predictions using the trained model
        Dataset<Row> predictions = trainedModel.transform(predictionData);

        // Collect predictions as a double array
        return predictions.select("prediction")
                .javaRDD()
                .map(row -> row.getDouble(0)) // Extract prediction values
                .collect()
                .stream()
                .mapToDouble(Double::doubleValue) // Convert Double to double
                .toArray();
    }

    @Override
    public ModelMetrics evaluate(double[][] testData, double[] testLabels) {
        // Define schema for the test DataFrame
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("label", DataTypes.DoubleType, false),
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert the test data to Row format
        List<Row> rows = IntStream.range(0, testData.length)
                .mapToObj(i -> RowFactory.create(testLabels[i], Vectors.dense(testData[i])))
                .collect(Collectors.toList());

        // Create DataFrame for testing
        Dataset<Row> testDataFrame = spark.createDataFrame(rows, schema);

        // Generate predictions on test data
        Dataset<Row> predictions = trainedModel.transform(testDataFrame);

        // Evaluate the model
        RegressionEvaluator evaluator = new RegressionEvaluator()
                .setLabelCol("label")
                .setPredictionCol("prediction");

        double mse = evaluator.setMetricName("mse").evaluate(predictions);
        double rmse = Math.sqrt(mse); // Root Mean Squared Error

        // Return ModelMetrics with RMSE; precision, recall, fMeasure are not applicable for regression
        return new ModelMetrics(rmse, 0, 0, 0, 0);
    }
}

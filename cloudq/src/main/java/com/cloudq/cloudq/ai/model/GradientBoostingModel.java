package com.cloudq.cloudq.ai.model;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.ml.classification.GBTClassifier;
import org.apache.spark.ml.classification.GBTClassificationModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;

public class GradientBoostingModel extends AIModel {
    private GBTClassificationModel trainedModel;
    private SparkSession spark;

    public GradientBoostingModel(SparkSession spark) {
        this.spark = spark;
    }

    @Override
    public void train(double[][] data, double[] labels) {
        // Define schema for the training data
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("label", DataTypes.DoubleType, false),
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert data to rows for DataFrame creation
        Row[] rows = new Row[data.length];
        for (int i = 0; i < data.length; i++) {
            org.apache.spark.ml.linalg.Vector featureVector = Vectors.dense(data[i]);
            rows[i] = RowFactory.create(labels[i], featureVector);
        }

        // Create DataFrame and train model
        Dataset<Row> trainingData = spark.createDataFrame(Arrays.asList(rows), schema);
        GBTClassifier classifier = new GBTClassifier().setLabelCol("label").setFeaturesCol("features");
        this.trainedModel = classifier.fit(trainingData);
    }

    @Override
    public double[] predict(double[][] data) {
        // Define schema for prediction data
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert data to rows
        Row[] rows = new Row[data.length];
        for (int i = 0; i < data.length; i++) {
            org.apache.spark.ml.linalg.Vector featureVector = Vectors.dense(data[i]);
            rows[i] = RowFactory.create(featureVector);
        }

        // Create DataFrame and make predictions
        Dataset<Row> predictionData = spark.createDataFrame(Arrays.asList(rows), schema);
        Dataset<Row> predictions = trainedModel.transform(predictionData);

        // Extract predictions
        int predictionCount = (int) predictions.count();
        double[] result = new double[predictionCount];
        for (int i = 0; i < predictionCount; i++) {
            result[i] = predictions.collectAsList().get(i).getDouble(predictions.schema().fieldIndex("prediction"));
        }
        return result;
    }

    @Override
    public ModelMetrics evaluate(double[][] testData, double[] testLabels) {
        // Define schema for test data
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("label", DataTypes.DoubleType, false),
                DataTypes.createStructField("features", new org.apache.spark.ml.linalg.VectorUDT(), false)
        });

        // Convert test data to rows
        Row[] rows = new Row[testData.length];
        for (int i = 0; i < testData.length; i++) {
            org.apache.spark.ml.linalg.Vector featureVector = Vectors.dense(testData[i]);
            rows[i] = RowFactory.create(testLabels[i], featureVector);
        }

        // Create DataFrame and evaluate
        Dataset<Row> testDataFrame = spark.createDataFrame(Arrays.asList(rows), schema);
        Dataset<Row> predictions = trainedModel.transform(testDataFrame);

        // Model evaluation metrics
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("label")
                .setPredictionCol("prediction");

        double accuracy = evaluator.setMetricName("accuracy").evaluate(predictions);
        double precision = evaluator.setMetricName("weightedPrecision").evaluate(predictions);
        double recall = evaluator.setMetricName("weightedRecall").evaluate(predictions);
        double f1 = evaluator.setMetricName("f1").evaluate(predictions);

        // Use 'accuracy' as a placeholder for the fifth parameter
        return new ModelMetrics(accuracy, accuracy, precision, recall, f1);
    }
}

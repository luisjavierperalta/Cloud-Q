package com.cloudq.cloudq.ai.engine;


import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.ml.linalg.Vectors;

import java.util.Arrays;
import java.util.List;

public class DataPreprocessorJava {

    private SparkSession spark;

    public DataPreprocessorJava(SparkSession spark) {
        this.spark = spark;
    }

    /**
     * Handles missing values by filling with median values for numerical columns
     * and most frequent values for categorical columns.
     *
     * @param dataset - The dataset to preprocess
     * @return Dataset<Row> - Dataset with missing values handled
     */
    public Dataset<Row> handleMissingValues(Dataset<Row> dataset) {
        for (StructField field : dataset.schema().fields()) {
            if (field.dataType().equals(DataTypes.DoubleType) || field.dataType().equals(DataTypes.IntegerType)) {
                double median = dataset.stat().approxQuantile(field.name(), new double[]{0.5}, 0.0)[0];
                dataset = dataset.na().fill(median, new String[]{field.name()});
            } else if (field.dataType().equals(DataTypes.StringType)) {
                String mode = dataset.groupBy(field.name()).count()
                        .orderBy(functions.desc("count"))
                        .first().getString(0);
                dataset = dataset.na().fill(mode, new String[]{field.name()});
            }
        }
        return dataset;
    }

    /**
     * Encodes categorical columns to numeric values using a simple mapping approach.
     *
     * @param dataset - The dataset to preprocess
     * @param categoricalColumns - Array of categorical column names
     * @return Dataset<Row> - Dataset with encoded categorical features
     */
    public Dataset<Row> encodeCategoricalColumns(Dataset<Row> dataset, String[] categoricalColumns) {
        for (String column : categoricalColumns) {
            List<Row> uniqueValues = dataset.select(column).distinct().collectAsList();
            for (int i = 0; i < uniqueValues.size(); i++) {
                String value = uniqueValues.get(i).getString(0);
                dataset = dataset.withColumn(column + "_indexed",
                        functions.when(functions.col(column).equalTo(value), i).otherwise(functions.col(column + "_indexed")));
            }
            dataset = dataset.drop(column); // Drop the original column
        }
        return dataset;
    }

    /**
     * Scales numerical features manually by standardizing them.
     *
     * @param dataset - The dataset to preprocess
     * @param featureColumns - Array of numerical feature column names
     * @return Dataset<Row> - Dataset with scaled numerical features
     */
    public Dataset<Row> scaleNumericalFeatures(Dataset<Row> dataset, String[] featureColumns) {
        for (String column : featureColumns) {
            double mean = dataset.agg(functions.avg(column)).first().getDouble(0);
            double stdDev = dataset.agg(functions.stddev(column)).first().getDouble(0);
            dataset = dataset.withColumn(column + "_scaled",
                    (functions.col(column).minus(mean)).divide(stdDev));
        }
        return dataset;
    }

    /**
     * Splits the dataset into training and testing sets.
     *
     * @param dataset - The dataset to split
     * @param trainRatio - Ratio of training data (e.g., 0.8 for 80% training data)
     * @return Dataset<Row>[] - Array where index 0 is training data and index 1 is test data
     */
    public Dataset<Row>[] splitData(Dataset<Row> dataset, double trainRatio) {
        return dataset.randomSplit(new double[]{trainRatio, 1 - trainRatio});
    }

    /**
     * Main method for running the full preprocessing pipeline.
     * This method handles missing values, encodes categorical data, scales numerical features,
     * and splits the data.
     *
     * @param dataset - The raw dataset to preprocess
     * @param categoricalColumns - Array of categorical column names
     * @param numericalColumns - Array of numerical column names
     * @param trainRatio - Ratio of data to use for training
     * @return Dataset<Row>[] - Preprocessed training and testing datasets
     */
    public Dataset<Row>[] preprocessData(Dataset<Row> dataset, String[] categoricalColumns, String[] numericalColumns, double trainRatio) {
        dataset = handleMissingValues(dataset);
        dataset = encodeCategoricalColumns(dataset, categoricalColumns);
        dataset = scaleNumericalFeatures(dataset, numericalColumns);

        // Split dataset into training and testing sets
        return splitData(dataset, trainRatio);
    }

    public Dataset<Row> normalizeFeatures(Dataset<Row> cleanedData) {
        return cleanedData;
    }
}

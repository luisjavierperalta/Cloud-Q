package com.cloudq.cloudq.ai.engine;


import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.feature.StandardScaler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;

public class DataPreprocessor {

    private SparkSession spark;

    public DataPreprocessor(SparkSession spark) {
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
     * Encodes categorical columns to numeric values using StringIndexer.
     *
     * @param dataset - The dataset to preprocess
     * @param categoricalColumns - Array of categorical column names
     * @return Dataset<Row> - Dataset with encoded categorical features
     */
    public Dataset<Row> encodeCategoricalColumns(Dataset<Row> dataset, String[] categoricalColumns) {
        for (String column : categoricalColumns) {
            StringIndexer indexer = new StringIndexer()
                    .setInputCol(column)
                    .setOutputCol(column + "_indexed");
            dataset = indexer.fit(dataset).transform(dataset).drop(column);
        }
        return dataset;
    }

    /**
     * Scales numerical features using StandardScaler.
     *
     * @param dataset - The dataset to preprocess
     * @param featureColumns - Array of numerical feature column names
     * @return Dataset<Row> - Dataset with scaled numerical features
     */
    public Dataset<Row> scaleNumericalFeatures(Dataset<Row> dataset, String[] featureColumns) {
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(featureColumns)
                .setOutputCol("features_unscaled");

        dataset = assembler.transform(dataset);

        StandardScaler scaler = new StandardScaler()
                .setInputCol("features_unscaled")
                .setOutputCol("features_scaled")
                .setWithMean(true)
                .setWithStd(true);

        dataset = scaler.fit(dataset).transform(dataset).drop("features_unscaled");

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

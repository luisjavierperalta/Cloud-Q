package com.cloudq.cloudq.ai.engine;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.dataset.DataSet;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrainingData {
    private INDArray features; // Feature matrix
    private INDArray labels;   // Label matrix

    // Constructor for initializing with features and labels
    public TrainingData(INDArray features, INDArray labels) {
        this.features = features;
        this.labels = labels;
    }

    // Method to create a DataSet from features and labels
    public DataSet toDataSet() {
        return new DataSet(features, labels);
    }

    // Getters and Setters
    public INDArray getFeatures() {
        return features;
    }

    public void setFeatures(INDArray features) {
        this.features = features;
    }

    public INDArray getLabels() {
        return labels;
    }

    public void setLabels(INDArray labels) {
        this.labels = labels;
    }

    // Method to load training data from a CSV file
    public static List<TrainingData> loadDataFromCsv(String filePath) {
        List<TrainingData> trainingDataList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // Assuming the last column is the label and all others are features
                double[] features = new double[nextLine.length - 1];
                for (int i = 0; i < nextLine.length - 1; i++) {
                    features[i] = Double.parseDouble(nextLine[i]);
                }
                double label = Double.parseDouble(nextLine[nextLine.length - 1]);

                INDArray featureArray = Nd4j.create(features);
                INDArray labelArray = Nd4j.create(new double[]{label}); // Assuming binary classification; adjust as needed

                trainingDataList.add(new TrainingData(featureArray, labelArray));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace(); // Handle the exception as needed
        }

        return trainingDataList;
    }

    // Utility method to convert lists to INDArray
    public static TrainingData fromLists(List<double[]> featureList, List<double[]> labelList) {
        INDArray features = Nd4j.create(featureList.toArray(new double[0][]));
        INDArray labels = Nd4j.create(labelList.toArray(new double[0][]));
        return new TrainingData(features, labels);
    }
}

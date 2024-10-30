package com.cloudq.cloudq.ai.model;

import weka.classifiers.trees.RandomForest;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Attribute;
import weka.classifiers.Evaluation;

import java.util.ArrayList;
import java.util.Arrays;

public class RandomForestModel extends AIModel {
    private RandomForest model;

    public RandomForestModel() {
        model = new RandomForest();
    }

    @Override
    public void train(double[][] data, double[] labels) {
        Instances dataset = new Instances("Dataset", createAttributes(data[0].length), data.length);

        // Create instances
        for (int i = 0; i < data.length; i++) {
            DenseInstance instance = new DenseInstance(data[0].length + 1); // +1 for class label
            for (int j = 0; j < data[i].length; j++) {
                instance.setValue(j, data[i][j]);
            }
            instance.setClassValue(labels[i]);
            dataset.add(instance);
        }

        dataset.setClassIndex(data[0].length); // Set the class index to the last attribute
        try {
            model.buildClassifier(dataset); // Build the model
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public double[] predict(double[][] data) {
        double[] predictions = new double[data.length];
        Instances dataset = new Instances("Dataset", createAttributes(data[0].length), 0); // Empty dataset

        // Create instances
        for (int i = 0; i < data.length; i++) {
            DenseInstance instance = new DenseInstance(data[0].length + 1); // +1 for class label
            for (int j = 0; j < data[i].length; j++) {
                instance.setValue(j, data[i][j]);
            }
            instance.setDataset(dataset); // Set dataset
            dataset.add(instance);
            try {
                predictions[i] = model.classifyInstance(instance); // Predict class label
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return predictions;
    }

    @Override
    public ModelMetrics evaluate(double[][] testData, double[] testLabels) {
        Instances testDataset = new Instances("TestDataset", createAttributes(testData[0].length), testData.length);

        // Create test instances
        for (int i = 0; i < testData.length; i++) {
            DenseInstance instance = new DenseInstance(testData[0].length + 1); // +1 for class label
            for (int j = 0; j < testData[i].length; j++) {
                instance.setValue(j, testData[i][j]);
            }
            instance.setClassValue(testLabels[i]);
            testDataset.add(instance);
        }

        testDataset.setClassIndex(testData[0].length); // Set the class index to the last attribute

        try {
            Evaluation evaluation = new Evaluation(testDataset);
            evaluation.evaluateModel(model, testDataset);

            // Return ModelMetrics with five arguments as required
            return new ModelMetrics(
                    evaluation.correct(),
                    evaluation.incorrect(),
                    evaluation.precision(1),
                    evaluation.recall(1),
                    evaluation.fMeasure(1)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return empty metrics if evaluation fails
        return new ModelMetrics();
    }

    private ArrayList<Attribute> createAttributes(int numAttributes) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < numAttributes; i++) {
            attributes.add(new Attribute("feature" + i));
        }
        // Example classes, update as per your use case
        ArrayList<String> classValues = new ArrayList<>(Arrays.asList("class1", "class2"));
        attributes.add(new Attribute("class", classValues)); // Add the class attribute
        return attributes;
    }
}

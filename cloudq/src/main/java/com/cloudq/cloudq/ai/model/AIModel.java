// AIModel.java
package com.cloudq.cloudq.ai.model;

public abstract class AIModel {
    public abstract void train(double[][] data, double[] labels);
    public abstract double[] predict(double[][] data);
    public abstract ModelMetrics evaluate(double[][] testData, double[] testLabels);
}



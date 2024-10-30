package com.cloudq.cloudq.ai.model;

public class ModelMetrics {
    private double costDeviation;
    private double performanceDeviation;
    private double accuracy;
    private double correct; // Optional: correct predictions
    private double incorrect; // Optional: incorrect predictions
    private double precision; // Optional: precision
    private double recall; // Optional: recall
    private double fMeasure; // Optional: F-measure

    // Constructor for initializing metrics
    public ModelMetrics(double costDeviation, double performanceDeviation, double accuracy, double f1, double v) {
        this.costDeviation = costDeviation;
        this.performanceDeviation = performanceDeviation;
        this.accuracy = accuracy;
    }

    // No-argument constructor for default initialization
    public ModelMetrics() {
        this.costDeviation = 0.0;
        this.performanceDeviation = 0.0;
        this.accuracy = 0.0;
        this.correct = 0.0;
        this.incorrect = 0.0;
        this.precision = 0.0;
        this.recall = 0.0;
        this.fMeasure = 0.0;
    }

    // Setters
    public void setCostDeviation(double costDeviation) {
        this.costDeviation = costDeviation;
    }

    public void setPerformanceDeviation(double performanceDeviation) {
        this.performanceDeviation = performanceDeviation;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public void setCorrect(double correct) {
        this.correct = correct;
    }

    public void setIncorrect(double incorrect) {
        this.incorrect = incorrect;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public void setFMeasure(double fMeasure) {
        this.fMeasure = fMeasure;
    }

    // Getters
    public double getCostDeviation() {
        return costDeviation;
    }

    public double getPerformanceDeviation() {
        return performanceDeviation;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getCorrect() {
        return correct;
    }

    public double getIncorrect() {
        return incorrect;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getFMeasure() {
        return fMeasure;
    }

    // toString method for easy logging and debugging
    @Override
    public String toString() {
        return "ModelMetrics{" +
                "costDeviation=" + costDeviation +
                ", performanceDeviation=" + performanceDeviation +
                ", accuracy=" + accuracy +
                ", correct=" + correct +
                ", incorrect=" + incorrect +
                ", precision=" + precision +
                ", recall=" + recall +
                ", fMeasure=" + fMeasure +
                '}';
    }
}

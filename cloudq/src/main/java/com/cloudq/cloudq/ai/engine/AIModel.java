package com.cloudq.cloudq.ai.engine;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration; // Correct import
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AIModel {
    private static final Logger logger = Logger.getLogger(AIModel.class.getName());
    private MultiLayerNetwork model;
    private int inputSize;
    private int outputSize;

    // Constructor to initialize the model with input and output sizes
    public AIModel(int inputSize, int outputSize) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.model = createModel();
    }

    // Method to create and configure the neural network model
    private MultiLayerNetwork createModel() {
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam(0.001)) // Learning rate
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(inputSize) // Number of input features
                        .nOut(10) // Number of neurons in the hidden layer
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) // Use the correct method for LossFunction
                        .activation(Activation.SOFTMAX) // Change to appropriate activation for your output
                        .nIn(10) // Input size from the previous layer
                        .nOut(outputSize) // Number of output classes
                        .build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();
        model.setListeners(new ScoreIterationListener(100)); // Print score every 100 iterations
        return model;
    }

    // Method to train the model with given training data
    public void train(DataSetIterator trainData, int numEpochs) {
        for (int i = 0; i < numEpochs; i++) {
            model.fit(trainData);
            logger.log(Level.INFO, "Epoch " + (i + 1) + " completed.");
        }
        logger.log(Level.INFO, "Training completed.");
    }

    // Method to make predictions using the trained model
    public INDArray predict(INDArray input) {
        return model.output(input);
    }

    // Method to evaluate the model on a test dataset
    public double evaluate(DataSetIterator testData) {
        double accuracy = 0.0;
        int totalExamples = 0; // To calculate total examples for accuracy
        while (testData.hasNext()) {
            DataSet testBatch = testData.next();
            INDArray output = model.output(testBatch.getFeatures());
            accuracy += calculateAccuracy(output, testBatch.getLabels());
            totalExamples += testBatch.numExamples(); // Total examples in the current batch
        }
        accuracy /= totalExamples; // Average accuracy over the total examples
        logger.log(Level.INFO, "Evaluation completed. Accuracy: " + accuracy);
        return accuracy;
    }

    // Calculate accuracy (dummy implementation, adjust as needed)
    private double calculateAccuracy(INDArray output, INDArray labels) {
        int correctPredictions = 0;
        for (int i = 0; i < output.rows(); i++) {
            if (output.getRow(i).argMax(1).getInt(0) == labels.getRow(i).argMax(1).getInt(0)) {
                correctPredictions++;
            }
        }
        return (double) correctPredictions / output.rows();
    }

    // Getters for input and output sizes
    public int getInputSize() {
        return inputSize;
    }

    public int getOutputSize() {
        return outputSize;
    }
}

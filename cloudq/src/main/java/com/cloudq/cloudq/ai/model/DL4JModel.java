package com.cloudq.cloudq.ai.model;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.eval.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

public class DL4JModel extends AIModel {
    private final MultiLayerNetwork model;

    public DL4JModel(MultiLayerNetwork model) {
        this.model = model;
        this.model.setListeners(new ScoreIterationListener(10)); // Log every 10 iterations
    }

    @Override
    public void train(double[][] data, double[] labels) {
        INDArray input = Nd4j.create(data);
        INDArray output = Nd4j.create(labels.length, 1);
        for (int i = 0; i < labels.length; i++) {
            output.putScalar(new int[]{i, 0}, labels[i]);
        }

        DataSet dataset = new DataSet(input, output);
        model.fit(dataset);
    }

    @Override
    public double[] predict(double[][] data) {
        INDArray input = Nd4j.create(data);
        INDArray predictions = model.output(input);
        double[] results = new double[predictions.rows()];

        for (int i = 0; i < predictions.rows(); i++) {
            results[i] = predictions.getRow(i).argMax(1).getDouble(0); // Get class with highest probability
        }
        return results;
    }

    @Override
    public ModelMetrics evaluate(double[][] testData, double[] testLabels) {
        INDArray input = Nd4j.create(testData);
        INDArray trueLabels = Nd4j.create(testLabels.length, 1);

        for (int i = 0; i < testLabels.length; i++) {
            trueLabels.putScalar(new int[]{i, 0}, testLabels[i]);
        }

        INDArray predictions = model.output(input);
        Evaluation eval = new Evaluation();
        eval.eval(trueLabels, predictions);

        double accuracy = eval.accuracy();
        double precision = eval.precision();
        double recall = eval.recall();
        double f1 = eval.f1();

        return new ModelMetrics(accuracy, precision, recall, f1, accuracy);
    }
}

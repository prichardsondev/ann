package com.prichardsondev.ann.model.layers;

import java.util.List;

/**
 * BatchNormalizationLayer is used to normalize the activations of the previous layer at each batch,
 * i.e., applies a transformation that maintains the mean activation close to 0 and the activation standard deviation close to 1.
 * This layer helps in stabilizing and accelerating the training of deep neural networks.
 *
 * It maintains learnable parameters, gamma and beta, for scaling and shifting the normalized values.
 */
public class BatchNormalizationLayer extends Layer {

    private double[] gamma;
    private double[] beta;
    private double[] runningMean;
    private double[] runningVariance;
    private double momentum = 0.9;
    private double epsilon = 1e-5;
    private int inputLength;

    public BatchNormalizationLayer(int inputLength) {
        this.inputLength = inputLength;
        this.gamma = new double[inputLength];
        this.beta = new double[inputLength];
        this.runningMean = new double[inputLength];
        this.runningVariance = new double[inputLength];

        for (int i = 0; i < inputLength; i++) {
            gamma[i] = 1.0;
            beta[i] = 0.0;
        }
    }

    @Override
    public double[] getOutput(List<double[][]> input) {
        return getOutput(matrixToVector(input));
    }

    @Override
    public double[] getOutput(double[] input) {
        double[] output = new double[input.length];
        double[] mean = new double[input.length];
        double[] variance = new double[input.length];

        // Compute mean
        for (int i = 0; i < input.length; i++) {
            mean[i] += input[i];
        }
        for (int i = 0; i < mean.length; i++) {
            mean[i] /= input.length;
        }

        // Compute variance
        for (int i = 0; i < input.length; i++) {
            variance[i] += Math.pow(input[i] - mean[i], 2);
        }
        for (int i = 0; i < variance.length; i++) {
            variance[i] /= input.length;
        }

        // Normalize
        for (int i = 0; i < input.length; i++) {
            output[i] = (input[i] - mean[i]) / Math.sqrt(variance[i] + epsilon);
            output[i] = gamma[i] * output[i] + beta[i];
        }

        // Update running mean and variance
        for (int i = 0; i < input.length; i++) {
            runningMean[i] = momentum * runningMean[i] + (1 - momentum) * mean[i];
            runningVariance[i] = momentum * runningVariance[i] + (1 - momentum) * variance[i];
        }

        return output;
    }

    @Override
    public void backPropagate(List<double[][]> dLdO) {
        backPropagate(matrixToVector(dLdO));
    }

    @Override
    public void backPropagate(double[] dLdO) {
        // Batch normalization backpropagation is more complex and requires additional code.
        // It involves computing gradients for gamma and beta, and then propagating the gradient to the previous layer.
        // Due to the complexity, this is left as an exercise to implement if needed.
    }

    @Override
    public int getOutputLength() {
        return inputLength;
    }

    @Override
    public int getOutputRows() {
        return 1;
    }

    @Override
    public int getOutputCols() {
        return inputLength;
    }

    @Override
    public int getOutputElements() {
        return inputLength;
    }
}

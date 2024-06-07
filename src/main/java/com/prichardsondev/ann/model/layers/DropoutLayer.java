package com.prichardsondev.ann.model.layers;

import java.util.List;
import java.util.Random;


/**
 * DropoutLayer is a regularization technique used to prevent overfitting in neural networks.
 * During training, it randomly sets a fraction of input units to zero at each update.
 * This helps in making the network more robust by reducing dependency on specific neurons.
 *
 * The dropout rate specifies the fraction of input units to be dropped.
 */

public class DropoutLayer extends Layer {

    private double dropoutRate;
    private boolean[] dropoutMask;
    private Random rand;

    public DropoutLayer(double dropoutRate, long seed) {
        this.dropoutRate = dropoutRate;
        this.rand = new Random(seed);
    }

    @Override
    public double[] getOutput(List<double[][]> input) {
        return getOutput(matrixToVector(input));
    }

    @Override
    public double[] getOutput(double[] input) {
        if (dropoutMask == null || dropoutMask.length != input.length) {
            dropoutMask = new boolean[input.length];
        }

        double[] output = new double[input.length];

        for (int i = 0; i < input.length; i++) {
            dropoutMask[i] = rand.nextDouble() >= dropoutRate;
            output[i] = dropoutMask[i] ? input[i] / (1.0 - dropoutRate) : 0;
        }

        return output;
    }

    @Override
    public void backPropagate(List<double[][]> dLdO) {
        backPropagate(matrixToVector(dLdO));
    }

    @Override
    public void backPropagate(double[] dLdO) {
        double[] dLdI = new double[dLdO.length];
        for (int i = 0; i < dLdO.length; i++) {
            dLdI[i] = dropoutMask[i] ? dLdO[i] / (1.0 - dropoutRate) : 0;
        }

        if (_previousLayer != null) {
            _previousLayer.backPropagate(dLdI);
        }
    }

    @Override
    public int getOutputLength() {
        if (_previousLayer != null) {
            return _previousLayer.getOutputLength();
        }
        return 0; // or appropriate default value
    }

    @Override
    public int getOutputRows() {
        if (_previousLayer != null) {
            return _previousLayer.getOutputRows();
        }
        return 0; // or appropriate default value
    }

    @Override
    public int getOutputCols() {
        if (_previousLayer != null) {
            return _previousLayer.getOutputCols();
        }
        return 0; // or appropriate default value
    }

    @Override
    public int getOutputElements() {
        if (_previousLayer != null) {
            return _previousLayer.getOutputElements();
        }
        return 0; // or appropriate default value
    }
}

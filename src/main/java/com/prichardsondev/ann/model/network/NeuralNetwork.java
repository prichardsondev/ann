package com.prichardsondev.ann.model.network;



import com.prichardsondev.ann.model.data.Image;
import com.prichardsondev.ann.model.layers.Layer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.prichardsondev.ann.model.data.MatrixUtility.add;
import static com.prichardsondev.ann.model.data.MatrixUtility.multiply;


public class NeuralNetwork implements Serializable {

    public List<Layer> get_layers() {
        return _layers;
    }

    private List<Layer> _layers;
    private double _scaleFactor;

    public NeuralNetwork(List<Layer> layers, double scaleFactor) {
        _layers = layers;
        _scaleFactor = scaleFactor;
        linkLayers();
    }

    private void linkLayers() {
        if (_layers.size() <= 1) return;
        for (int i = 0; i < _layers.size(); i++) {
            if (i == 0)
                _layers.get(i).set_nextLayer(_layers.get(i + 1));
            else if (i == _layers.size() - 1)
                _layers.get(i).set_previousLayer(_layers.get(i - 1));
            else {
                _layers.get(i).set_previousLayer(_layers.get(i - 1));
                _layers.get(i).set_nextLayer(_layers.get(i + 1));
            }
        }
    }

    public double[] getErrors(double[] networkOutput, int correctAnswer) {
        double[] expected = new double[networkOutput.length];
        expected[correctAnswer] = 1;
        return add(networkOutput, multiply(expected, -1));
    }

    private int getMaxIndex(double[] in) {
        double max = in[0];
        int maxIndex = 0;
        for (int i = 1; i < in.length; i++) {
            if (in[i] > max) {
                max = in[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public int guess(Image image) {
        try {
            List<double[][]> inList = new ArrayList<>();
            inList.add(multiply(image.getData(), (1 / _scaleFactor)));
            double[] output = _layers.get(0).getOutput(inList);
            return getMaxIndex(output);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float test(List<Image> images) {
        int correct = 0;
        for (Image image : images) {
            int guess = guess(image);
            if (guess == image.getLabel())
                correct++;
        }
        return (float) correct / images.size();
    }

    public void train(List<Image> images) {
        for (Image image : images) {
            List<double[][]> inList = new ArrayList<>();
            inList.add(multiply(image.getData(), (1 / _scaleFactor)));
            double[] output = _layers.get(0).getOutput(inList);
            double[] dLdO = getErrors(output, image.getLabel());
            _layers.get(_layers.size() - 1).backPropagate(dLdO);
        }
    }
}

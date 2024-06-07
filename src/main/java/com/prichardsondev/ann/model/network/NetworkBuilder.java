package com.prichardsondev.ann.model.network;

import com.prichardsondev.ann.model.layers.*;
import java.util.ArrayList;
import java.util.List;

public class NetworkBuilder {

    private NeuralNetwork _net;
    private int _inputRows;
    private int _inputCols;
    private double _scaleFactor;

    List<Layer> _layers;

    public NetworkBuilder(int inputRows, int inputCols, double scaleFactor) {
        _inputRows = inputRows;
        _inputCols = inputCols;
        _scaleFactor = scaleFactor;
        _layers = new ArrayList<>();
    }

    public void addConvolutionLayer(int numFilters, int filterSize, int stepSize, double learningRate, long seed) {
        if (_layers.isEmpty())
            _layers.add(new ConvolutionLayer(filterSize, stepSize, 1, _inputRows, _inputCols, seed, numFilters, learningRate));
        else
            _layers.add(new ConvolutionLayer(filterSize, stepSize, _layers.get(_layers.size() - 1).getOutputLength(),
                    _layers.get(_layers.size() - 1).getOutputRows(), _layers.get(_layers.size() - 1).getOutputCols(),
                    seed, numFilters, learningRate));
    }

    public void addMaxPoolLayer(int windowSize, int stepSize) {
        if (_layers.isEmpty())
            _layers.add(new MaxPoolLayer(stepSize, windowSize, 1, _inputRows, _inputCols));
        else
            _layers.add(new MaxPoolLayer(stepSize, windowSize, _layers.get(_layers.size() - 1).getOutputLength(),
                    _layers.get(_layers.size() - 1).getOutputRows(), _layers.get(_layers.size() - 1).getOutputCols()));
    }

    public void addFullyConnectedLayer(int outLength, double learningRate, long seed) {
        if (_layers.isEmpty())
            _layers.add(new FullyConnectedLayer(_inputCols * _inputRows, outLength, seed, learningRate));
        else
            _layers.add(new FullyConnectedLayer(_layers.get(_layers.size() - 1).getOutputElements(),
                    outLength, seed, learningRate));
    }

    public void addBatchNormalizationLayer(int inputLength) {
        if (!_layers.isEmpty())
            _layers.add(new BatchNormalizationLayer(inputLength));
    }

    public void addDropoutLayer(double dropoutRate, long seed) {
        if (!_layers.isEmpty())
            _layers.add(new DropoutLayer(dropoutRate, seed));
    }

    public NeuralNetwork build() {
        _net = new NeuralNetwork(_layers, _scaleFactor);
        return _net;
    }
}



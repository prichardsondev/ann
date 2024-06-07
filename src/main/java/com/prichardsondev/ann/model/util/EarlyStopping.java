package com.prichardsondev.ann.model.util;


import com.prichardsondev.ann.model.network.NeuralNetwork;

public class EarlyStopping {
    private int patience;
    private int wait;
    private double bestLoss;
    private NeuralNetwork bestModel;

    public EarlyStopping(int patience) {
        this.patience = patience;
        this.wait = 0;
        this.bestLoss = Double.MAX_VALUE;
    }

    public boolean shouldStop(double currentLoss, NeuralNetwork currentModel) {
        if (currentLoss < bestLoss) {
            bestLoss = currentLoss;
            wait = 0;
            bestModel = currentModel;
            return false;
        } else {
            wait++;
            if (wait >= patience) {
                return true;
            }
            return false;
        }
    }

    public NeuralNetwork getBestModel() {
        return bestModel;
    }
}

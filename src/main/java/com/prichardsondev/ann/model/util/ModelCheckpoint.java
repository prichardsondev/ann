package com.prichardsondev.ann.model.util;


import com.prichardsondev.ann.model.network.NeuralNetwork;

import java.io.*;

public class ModelCheckpoint {

    public static void saveModel(NeuralNetwork model, String filepath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            oos.writeObject(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NeuralNetwork loadModel(String filepath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
            return (NeuralNetwork) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

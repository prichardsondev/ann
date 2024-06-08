package com.prichardsondev.ann;

import com.prichardsondev.ann.model.data.DataReader;
import com.prichardsondev.ann.model.data.Image;
import com.prichardsondev.ann.model.layers.ConvolutionLayer;
import com.prichardsondev.ann.model.layers.FullyConnectedLayer;
import com.prichardsondev.ann.model.network.NetworkBuilder;
import com.prichardsondev.ann.model.network.NeuralNetwork;
import com.prichardsondev.ann.model.util.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.prichardsondev.ann.model.util.ModelCheckpoint.loadModel;
import static com.prichardsondev.ann.model.util.ModelCheckpoint.saveModel;
import static java.util.Collections.shuffle;

public class TrainNetwork {
    private static final String DATA_DIR = "src/main/data/";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        long seed = 123;

        /*To train and serialize the model, uncomment the following lines:*/

        //trainAndSerializeModel(seed);

        /*To retrain and serialize the model, uncomment the following lines:*/

        fineTuneAndSerializeModel(seed);

        /*To deserialize and test the model, uncomment the following lines:*/

        //testModel();

    }

    private static void trainAndSerializeModel1(long seed) throws IOException {
        //set data to train on
        String modelName = "train_augmented";
        List<Image> train = DataReader.readData(DATA_DIR + "train_augmented.csv");
        System.out.println("Training data loaded: " + train.size() + " records");
        List<Image> test = DataReader.readData(DATA_DIR + "test_augmented.csv");
        System.out.println("Evaluation data loaded: " + test.size() + " records");
        System.out.println();

        //set network architecture
        double lrc = .35;
        double lrf = .35;

        shuffle(train);
        float rate = 0.0f;
        NeuralNetwork net = null;
        //train model
        for (int j = 0; j < 10; j++) {
            EarlyStopping earlyStopping = new EarlyStopping(5); // Set patience for early stopping
            if(net==null) {
                net = getNeuralNetwork(seed, lrc, lrf);
            }
            else{
                double[] lr = getLeraningRate(net);
                net = getNeuralNetwork(seed, lr[0], lr[1]);
            }
            if(j!=0) adjustLeraningRate(net);

            int epochs = 10;
            for (int i = 1; i <= epochs; i++) {
                shuffle(train);

                net.train(train);
                rate = net.test(test);

                System.out.println(i + "," + rate);

                // Check for early stopping
                double valLoss = 1.0 - rate; // Assuming lower success rate indicates higher validation loss
                if (earlyStopping.shouldStop(valLoss, net)) {
                    System.out.println("Early stopping at epoch " + i);
                    net = earlyStopping.getBestModel();
                    break;
                }
            }
            //save final model with success rate
            String finalModelPath = String.format(DATA_DIR + "%s_%.3f_%.3f_%d_%.3f_model.ser",modelName, lrc, lrf, epochs, rate);
            saveModel(net, finalModelPath);
            System.out.println("Training complete and model serialized.");
            System.out.println();
        }
    }

    private static void trainAndSerializeModel(long seed) throws IOException {
        //set data to train on
        String modelName = "";
        List<Image> train = DataReader.readData(DATA_DIR + "train_augmented.csv");
        System.out.println("Training data loaded: " + train.size() + " records");
        List<Image> test = DataReader.readData(DATA_DIR + "test_augmented.csv");
        System.out.println("Evaluation data loaded: " + test.size() + " records");
        System.out.println();

        //set network architecture
        double lrc = .1;
        double lrf = .3;
        NeuralNetwork net = getNeuralNetwork(seed, lrc, lrf);
        EarlyStopping earlyStopping = new EarlyStopping(5); // Set patience for early stopping

        //pretraining test
        shuffle(train);
        float rate = 0.0f;
        rate = net.test(test);
        System.out.println("Epoch 0 - Pretraining");
        System.out.println("Test success rate: " + rate);
        System.out.println();

        //train model
        int epochs = 20;
        for (int i = 1; i <= epochs; i++) {
            shuffle(train);

            net.train(train);
            rate = net.test(test);
            String modelCheckpointPath = String.format(DATA_DIR + "model_checkpoint.ser");
            saveModel(net, modelCheckpointPath);
            System.out.println("Epoch " + i);
            System.out.println("Test success rate: " + rate);
            System.out.println("Model checkpoint saved after epoch " + i);
            System.out.println();

            // Check for early stopping
            double valLoss = 1.0 - rate; // Assuming lower success rate indicates higher validation loss
            if (earlyStopping.shouldStop(valLoss, net)) {
                System.out.println("Early stopping at epoch " + i);
                net = earlyStopping.getBestModel();
                break;
            }
        }
        //save final model with success rate
        String finalModelPath = String.format(DATA_DIR + "%s_%.3f_%.3f_%d_%.3f_model.ser",modelName, lrc, lrf, epochs, rate);
        saveModel(net, finalModelPath);
        System.out.println("Training complete and model serialized.");
    }

    private static void adjustLeraningRate(NeuralNetwork net) {
        ConvolutionLayer c = (ConvolutionLayer) net.get_layers().get(0);
        FullyConnectedLayer f = (FullyConnectedLayer) net.get_layers().get(2);

        c.set_learningRate(.38);
        f.set_learningRate(.38);



        System.out.println("Convolution Learning Rate = " + c.get_learningRate());
        System.out.println("Fully Connected Learning Rate = " + f.get_learningRate());
        System.out.println();
    }

    private static double[] getLeraningRate(NeuralNetwork net) {
        ConvolutionLayer c = (ConvolutionLayer) net.get_layers().get(0);
        FullyConnectedLayer f = (FullyConnectedLayer) net.get_layers().get(2);

        double[] lr = new double[2];
        lr[0] = c.get_learningRate();
        lr[1] = f.get_learningRate();

        return lr;
    }

    private static void fineTuneAndSerializeModel(long seed) throws IOException, ClassNotFoundException {
        List<Image> train = DataReader.readData(DATA_DIR + "train_augmented.csv");
        System.out.println("Training data size = " + train.size());
        List<Image> test = DataReader.readData(DATA_DIR + "test_augmented.csv");
        System.out.println("Test data size = " + test.size());

        // Load the pre-trained model
        String preTrainedModelPath = DATA_DIR + "model_checkpoint.ser";
        NeuralNetwork net = ModelCheckpoint.loadModel(preTrainedModelPath);
        if (net == null) {
            throw new IOException("Failed to load the pre-trained model.");
        }
        adjustLeraningRate(net);

        int epochs = 100; // Fine-tuning for fewer epochs might be sufficient
        EarlyStopping earlyStopping = new EarlyStopping(5); // Set patience for early stopping
        String modelCheckpointPath = DATA_DIR + "model_finetune_checkpoint.ser";
        float rate = 0.0f;
        for (int i = 0; i < epochs; i++) {
            System.out.println("Epoch " + i);
            shuffle(train);
            net.train(train);

            rate = net.test(test);
            System.out.println("Training success rate: " + rate);

            // Save model after each epoch
            ModelCheckpoint.saveModel(net, modelCheckpointPath);
            System.out.println("Model checkpoint saved after epoch " + i);
            System.out.println();
            // Check for early stopping
            double valLoss = 1.0 - rate; // Assuming lower success rate indicates higher validation loss
            if (earlyStopping.shouldStop(valLoss, net)) {
                System.out.println("Early stopping at epoch " + i);
                net = earlyStopping.getBestModel();
                break;
            }
        }

        saveModel(net, DATA_DIR + String.format("%.2f", rate) + "_" + "model_finetune.ser");
        System.out.println("Fine-tuning complete and model serialized.");
    }

    private static void testModel() {
        System.out.println("Loading data....");
        List<Image> test = DataReader.readData(DATA_DIR + "validate.csv");
        System.out.println("Test data size = " + test.size());
        NeuralNetwork net = ModelCheckpoint.loadModel(DATA_DIR + "model_checkpoint.ser");
        shuffle(test);
        float rate = Objects.requireNonNull(net).test(test);
        System.out.println("Training success rate: " + rate);
    }

    private static NeuralNetwork getNeuralNetwork(long seed, double lrc, double lrf) {

        NetworkBuilder builder = new NetworkBuilder(28, 28, 256 * 100);

        builder.addConvolutionLayer(8,5,1,lrc, seed);
        builder.addMaxPoolLayer(3,2);
        builder.addFullyConnectedLayer(10,lrf ,seed);

        return builder.build();
    }

    public static double randomFloat() {
        Random random = new Random();
        double min = 0.0001f;
        double max = 0.1f;
        return min + random.nextDouble() * (max - min);
    }
}

//